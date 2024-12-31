package com.cafe.inventory.controller;

import com.cafe.inventory.entity.InventoryItem;
import com.cafe.inventory.exception.InvalidInputException;
import com.cafe.inventory.service.InventoryService;
import com.cafe.inventory.service.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventory_ValidRequest() {
        // Arrange
        int page = 0;
        int size = 2;
        String[] sortBy = {"name"};
        String[] direction = {"asc"};
        PaginatedResponse<InventoryItem> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new InventoryItem(), new InventoryItem()), 0, 1, 2, 2);
        when(inventoryService.getAllInventoryItems(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<InventoryItem>> response = inventoryController.getAllInventoryItems(page, size, sortBy, direction);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void testGetAllInventory_InvalidRequest() {
        // Arrange
        int page = -1;
        int size = 0;
        String[] sortBy = {"name"};
        String[] direction = {"asc"};

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            inventoryController.getAllInventoryItems(page, size, sortBy, direction);
        });
    }

    @Test
    void testGetInventoryItemById_ValidRequest() {
        // Arrange
        Integer id = 1;
        InventoryItem inventoryItem = new InventoryItem();
        when(inventoryService.getInventoryItemById(id)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<InventoryItem> response = inventoryController.getInventoryItemById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testGetInventoryItemById_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            inventoryController.getInventoryItemById(id);
        });
    }

    @Test
    void testCreateInventoryItem_ValidRequest() {
        // Arrange
        InventoryItem inventoryItem = new InventoryItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(inventoryService.createInventoryItem(inventoryItem)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<InventoryItem> response = inventoryController.createInventoryItem(inventoryItem, result);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testCreateInventoryItem_InvalidRequest() {
        // Arrange
        InventoryItem inventoryItem = new InventoryItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "inventoryItem", "name", "Name is required"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            inventoryController.createInventoryItem(inventoryItem, result);
        });
    }

    @Test
    void testUpdateInventoryItem_ValidRequest() {
        // Arrange
        Integer id = 1;
        InventoryItem inventoryItem = new InventoryItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(inventoryService.updateInventoryItem(id, inventoryItem)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<InventoryItem> response = inventoryController.updateInventoryItem(id, inventoryItem, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testUpdateInventoryItem_InvalidRequest() {
        // Arrange
        Integer id = -1;
        InventoryItem inventoryItem = new InventoryItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            inventoryController.updateInventoryItem(id, inventoryItem, result);
        });
    }

    @Test
    void testDeleteInventoryItem_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(inventoryService).deleteInventoryItem(id);

        // Act
        ResponseEntity<InventoryItem> response = inventoryController.deleteInventoryItem(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteInventoryItem_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            inventoryController.deleteInventoryItem(id);
        });
    }
}
