package com.cafe.ordermanagement.dao;

import com.cafe.ordermanagement.entity.OrderMenuItemId;
import com.cafe.ordermanagement.entity.OrderMenuItemIdKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderMenuItemIdKeyDAOJPA extends JpaRepository<OrderMenuItemId, OrderMenuItemIdKey> {
    Optional<List<OrderMenuItemId>> findByOrderMenuItemIdKey_OrderId(Integer orderId);
}
