package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.entity.OrderMenuItemId;
import com.cafe.ordermanagement.entity.OrderMenuItemIdKey;

import java.util.List;

public interface OrderMenuItemIdKeyService {
    List<OrderMenuItemId> getOrderMenuItemIdKeyByOrderId(Integer order_id);
}
