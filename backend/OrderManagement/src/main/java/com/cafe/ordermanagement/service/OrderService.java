package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.dto.CategoryDTO;
import com.cafe.ordermanagement.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    PaginatedResponse<Order> getAllOrders(int page, int size, String[] sortBy, String[] direction);
    Order getOrderById(Integer id);
    List<Order> getOrdersByCustomerId(Integer customer_id);
    PaginatedResponse<MenuItemDTO> getAllMenuItems(
            int page, int size, String[] sortBy, String[] direction);
    PaginatedResponse<CategoryDTO> getAllMenuItemCategories(int page, int size, String[] sortBy, String[] direction);
    PaginatedResponse<MenuItemDTO> getAllMenuItemsByCategory(int page, int size, String[] sortBy, String[] direction, String category);
    Map<String, List<MenuItemDTO>> getMenuItemsGroupedByCategory(int page, int size, String[] sortBy, String[] direction);
    Order createOrder(Order menuItem);
    Order placeOrder(Integer customerId, List<Integer> menuItemIds, List<Integer> quantities);
    Order updateOrder(Integer id, Order menuItem);
    void deleteOrder(Integer id);
}
