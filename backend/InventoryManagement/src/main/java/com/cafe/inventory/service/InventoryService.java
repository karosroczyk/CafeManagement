package com.cafe.inventory.service;

import com.cafe.inventory.entity.InventoryItem;

import java.util.List;

public interface InventoryService {
    PaginatedResponse<InventoryItem> getAllInventoryItems(int page, int size, String[] sortBy, String[] direction);
    InventoryItem getInventoryItemById(Integer id);
    List<InventoryItem> getInventoryItemsByMenuItemIds(List<Integer> menuItemIds);
    public List<Boolean> areInventoryItemsByMenuItemIdsAvailable(List<Integer> menuItemIds, List<Integer> quantities);
    InventoryItem createInventoryItem(InventoryItem category);
    InventoryItem updateInventoryItem(Integer id, InventoryItem category);
    void deleteInventoryItem(Integer id);
    List<InventoryItem> updateStockByMenuItemIds(List<Integer> menuItemIds, List<Integer> quantities);
}
