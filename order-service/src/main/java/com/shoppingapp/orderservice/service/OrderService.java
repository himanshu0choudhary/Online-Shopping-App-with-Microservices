package com.shoppingapp.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.shoppingapp.orderservice.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingapp.orderservice.dto.OrderLineItemDto;
import com.shoppingapp.orderservice.dto.OrderRequest;
import com.shoppingapp.orderservice.dto.OrderResponse;
import com.shoppingapp.orderservice.model.Order;
import com.shoppingapp.orderservice.model.OrderLineItem;
import com.shoppingapp.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
        .stream()
        .map(this::mapFromDto)
        .toList();
        
        order.setOrderLineItemList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemList()
                                    .stream()
                                    .map(OrderLineItem::getSkuCode)
                                    .toList();


        // Check if item is in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri(
                        "http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build()
                )
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if(inventoryResponseArray == null || inventoryResponseArray.length == 0){
            throw new IllegalArgumentException("No Item Found");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::getIsInStock);

        if(allProductsInStock){
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    private OrderLineItem mapFromDto(OrderLineItemDto orderLineItemDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }

    private OrderResponse mapToDto(Order order){
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderLineItemDtoList(mapToDto(order.getOrderLineItemList()))
                .build();

    }

    private List<OrderLineItemDto> mapToDto(List<OrderLineItem> orderLineItems){
        return orderLineItems.stream()
                .map(orderLineItem -> OrderLineItemDto.builder()
                                    .skuCode(orderLineItem.getSkuCode())
                                    .price(orderLineItem.getPrice())
                                    .quantity(orderLineItem.getQuantity())
                                    .build()
                ).toList();
    }
}
