package com.cafe.menumanagement.service;

import com.cafe.menumanagement.entity.MenuItem;

public interface MenuItemService {
    PaginatedResponse<MenuItem> getAllMenuItems(int page, int size, String[] sortBy, String[] direction);
    PaginatedResponse<MenuItem> getMenuItemsByCategoryName(String categoryName, int page, int size, String[] sortBy, String[] direction);
    MenuItem getMenuItemById(Integer id);
    MenuItem createMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Integer id, MenuItem menuItem);
    void deleteMenuItem(Integer id);
}
