package com.shoppingapp.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingapp.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
