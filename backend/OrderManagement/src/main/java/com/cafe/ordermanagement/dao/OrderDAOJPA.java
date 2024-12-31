package com.cafe.ordermanagement.dao;

import com.cafe.ordermanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAOJPA extends JpaRepository<Order, Integer> {
}
