package com.shoppingapp.orderservice.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shoppingapp.orderservice.dto.OrderLineItemDto;
import com.shoppingapp.orderservice.dto.OrderRequest;
import com.shoppingapp.orderservice.dto.OrderResponse;
import com.shoppingapp.orderservice.model.Order;
import com.shoppingapp.orderservice.model.OrderLineItem;
import com.shoppingapp.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
        .stream()
        .map(orderLineItemDto -> mapFromDto(orderLineItemDto))
        .toList();
        
        order.setOrderLineItemList(orderLineItems);

        orderRepository.save(order);
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(order -> mapToDto(order))
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
