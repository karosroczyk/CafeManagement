package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.dto.MenuItem;
import com.cafe.ordermanagement.entity.Category;
import com.cafe.ordermanagement.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    PaginatedResponse<Order> getAllOrders(int page, int size, String[] sortBy, String[] direction);
    Order getOrderById(Integer id);
    PaginatedResponse<MenuItem> getAllMenuItems(
            int page, int size, String[] sortBy, String[] direction);
    PaginatedResponse<Category> getAllMenuItemCategories(int page, int size, String[] sortBy, String[] direction);
    PaginatedResponse<MenuItem> getAllMenuItemsByCategory(int page, int size, String[] sortBy, String[] direction, String category);
    Map<String, List<MenuItem>> getMenuItemsGroupedByCategory(int page, int size, String[] sortBy, String[] direction);
    Order createOrder(Order menuItem);
    Order placeOrder(Order order, List<Integer> menuItemIds, List<Integer> quantities);
    Order updateOrder(Integer id, Order menuItem);
    void deleteOrder(Integer id);
}
