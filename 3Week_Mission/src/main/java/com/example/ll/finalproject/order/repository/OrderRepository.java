package com.example.ll.finalproject.order.repository;

import com.example.ll.finalproject.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyerId(Long id);
}
