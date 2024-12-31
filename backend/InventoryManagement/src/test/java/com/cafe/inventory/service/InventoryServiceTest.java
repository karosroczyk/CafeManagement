package com.cafe.inventory.service;

import com.cafe.inventory.dao.InventoryDAOJPA;
import com.cafe.inventory.entity.InventoryItem;
import com.cafe.inventory.exception.DatabaseUniqueValidationException;
import com.cafe.inventory.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryDAOJPA inventoryDAOJPA;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventoryItems() {
        InventoryItem item1 = new InventoryItem(1, 101, 10, true);
        InventoryItem item2 = new InventoryItem(2, 102, 5, true);
        String[] sortingFields = {"menuItemId", "stockLevel"};
        String[] directions = {"asc", "desc"};

        List<Sort.Order> orders = Arrays.asList(
                new Sort.Order(Sort.Direction.ASC, "menuItemId"),
                new Sort.Order(Sort.Direction.DESC, "stockLevel")
        );
        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<InventoryItem> inventoryPage = new PageImpl<>(Arrays.asList(item1, item2));

        when(inventoryDAOJPA.findAll(pageable)).thenReturn(inventoryPage);
        PaginatedResponse<InventoryItem> result = inventoryService.getAllInventoryItems(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals(1, result.getData().get(0).getId());
        verify(inventoryDAOJPA, times(1)).findAll(pageable);
    }

    @Test
    void testGetInventoryItemById_Correct() {
        InventoryItem item = new InventoryItem(1, 101, 10, true);

        when(inventoryDAOJPA.findById(1)).thenReturn(Optional.of(item));
        InventoryItem result = inventoryService.getInventoryItemById(1);

        assertEquals(101, result.getMenuItemId());
        verify(inventoryDAOJPA, times(1)).findById(1);
    }

    @Test
    void testGetInventoryItemById_IdNotFound() {
        when(inventoryDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.getInventoryItemById(100)
        );

        assertEquals("InventoryItem with id: 100 not found.", exception.getMessage());
        verify(inventoryDAOJPA, times(1)).findById(100);
    }

    @Test
    void testCreateInventoryItem() {
        InventoryItem item = new InventoryItem(1, 101, 10, true);

        when(inventoryDAOJPA.save(item)).thenReturn(item);
        InventoryItem result = inventoryService.createInventoryItem(item);

        assertEquals(101, result.getMenuItemId());
        verify(inventoryDAOJPA, times(1)).save(item);
    }

    @Test
    void testCreateInventoryItem_DuplicateMenuItemId() {
        InventoryItem item = new InventoryItem(1, 101, 10, true);
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Duplicate entry");

        when(inventoryDAOJPA.save(item)).thenThrow(exception);

        DatabaseUniqueValidationException thrownException = assertThrows(
                DatabaseUniqueValidationException.class,
                () -> inventoryService.createInventoryItem(item)
        );

        assertEquals("Duplicate entry", thrownException.getMessage());
        verify(inventoryDAOJPA, times(1)).save(item);
    }

    @Test
    void testUpdateInventoryItem_Correct() {
        InventoryItem existingItem = new InventoryItem(1, 101, 10, true);
        InventoryItem updatedItem = new InventoryItem(1, 101, 15, false);

        when(inventoryDAOJPA.findById(1)).thenReturn(Optional.of(existingItem));
        when(inventoryDAOJPA.save(any(InventoryItem.class))).thenReturn(updatedItem);
        InventoryItem result = inventoryService.updateInventoryItem(1, updatedItem);

        assertEquals(15, result.getStockLevel());
        assertFalse(result.isAvailable());
        verify(inventoryDAOJPA, times(1)).findById(1);
        verify(inventoryDAOJPA, times(1)).save(existingItem);
    }

    @Test
    void testDeleteInventoryItem_Correct() {
        InventoryItem item = new InventoryItem(1, 101, 10, true);

        when(inventoryDAOJPA.findById(1)).thenReturn(Optional.of(item));
        doNothing().when(inventoryDAOJPA).deleteById(1);

        inventoryService.deleteInventoryItem(1);

        verify(inventoryDAOJPA, times(1)).findById(1);
        verify(inventoryDAOJPA, times(1)).deleteById(1);
    }

    @Test
    void testDeleteInventoryItem_IdNotFound() {
        when(inventoryDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.deleteInventoryItem(100)
        );

        assertEquals("InventoryItem with id: 100 not found.", exception.getMessage());
        verify(inventoryDAOJPA, times(1)).findById(100);
    }
}