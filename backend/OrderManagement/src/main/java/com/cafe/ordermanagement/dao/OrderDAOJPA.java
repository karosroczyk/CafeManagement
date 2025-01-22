package com.cafe.ordermanagement.dao;

import com.cafe.ordermanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDAOJPA extends JpaRepository<Order, Integer> {
    Optional<List<Order>> findOrdersByCustomerId(Integer customerId);
}
