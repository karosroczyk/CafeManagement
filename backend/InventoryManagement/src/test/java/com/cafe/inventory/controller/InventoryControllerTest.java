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
import java.util.List;
import java.util.Optional;

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
    void getInventoryItemAvailabilityByMenuItemIds(){
        List<Integer> menuItemIds = Arrays.asList(101, 102);
        List<Integer> quantities = Arrays.asList(5, 10);
        List<Boolean> results = Arrays.asList(true, true);

        when(inventoryService.areInventoryItemsByMenuItemIdsAvailable(menuItemIds, quantities)).thenReturn(results);
        ResponseEntity<List<Boolean>> response =
                inventoryController.getInventoryItemAvailabilityByMenuItemIds(menuItemIds, quantities);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(results, response.getBody());
    }

    @Test
    void getInventoryItemAvailabilityByMenuItemIds_ErrorCases(){
        List<Integer> menuItemIds = Arrays.asList(101, 102);

        InvalidInputException quantitiesOfMenuItemsIsNull = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.getInventoryItemAvailabilityByMenuItemIds(menuItemIds, null)
        );

        InvalidInputException quantitiesOfMenuItemsIsEmpty = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.getInventoryItemAvailabilityByMenuItemIds(menuItemIds, Arrays.asList())
        );

        assertEquals("Invalid id or quantity provided.", quantitiesOfMenuItemsIsNull.getMessage());
        verify(inventoryService, times(0)).reduceStockByMenuItemId(menuItemIds, null);
        assertEquals("Invalid id or quantity provided.", quantitiesOfMenuItemsIsEmpty.getMessage());
        verify(inventoryService, times(0)).reduceStockByMenuItemId(menuItemIds, Arrays.asList());
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

    @Test
    void testReduceStockByMenuItemId(){
        List<Integer> menuItemIds = Arrays.asList(101, 102);
        List<Integer> quantitiesOfMenuItems = Arrays.asList(5, 10);
        InventoryItem item1Updated = new InventoryItem(1, 101, 0, false);
        InventoryItem item2Updated = new InventoryItem(2, 102, 5, true);

        List<InventoryItem> updatedInventoryItemList = Arrays.asList(item1Updated, item2Updated);

        when(inventoryService.reduceStockByMenuItemId(menuItemIds, quantitiesOfMenuItems)).thenReturn(updatedInventoryItemList);
        ResponseEntity<List<InventoryItem>> response = this.inventoryController.reduceStockByMenuItemId(
                menuItemIds, quantitiesOfMenuItems);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedInventoryItemList, response.getBody());
    }

    @Test
    void testReduceStockByMenuItemId_ErrorCases(){
        List<Integer> quantitiesOfMenuItems = Arrays.asList(5, 10);

        InvalidInputException menuItemIdsIsNull = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.reduceStockByMenuItemId(null, quantitiesOfMenuItems)
        );

        InvalidInputException menuItemIdsIsEmpty = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.reduceStockByMenuItemId(Arrays.asList(), quantitiesOfMenuItems)
        );

        assertEquals("Invalid id or quantity provided.", menuItemIdsIsNull.getMessage());
        verify(inventoryService, times(0)).reduceStockByMenuItemId(null, quantitiesOfMenuItems);
        assertEquals("Invalid id or quantity provided.", menuItemIdsIsEmpty.getMessage());
        verify(inventoryService, times(0)).reduceStockByMenuItemId(Arrays.asList(), quantitiesOfMenuItems);
    }

    @Test
    void testAddStock(){
        InventoryItem item = new InventoryItem(1, 101, 20, true);

        when(inventoryService.addStock(1, 10)).thenReturn(item);
        ResponseEntity<InventoryItem> response = inventoryController.addStock(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item, response.getBody());
    }

    @Test
    void testAddStock_ErrorCases(){
        InvalidInputException exceptionQuantityLessThenZero = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.addStock(1, -5)
        );

        InvalidInputException exceptionIdLessThenZero = assertThrows(
                InvalidInputException.class,
                () -> inventoryController.addStock(-1, 5)
        );

        assertEquals("Invalid id: 1 or quantity -5 provided.", exceptionQuantityLessThenZero.getMessage());
        verify(inventoryService, times(0)).addStock(1, -5);
        assertEquals("Invalid id: -1 or quantity 5 provided.", exceptionIdLessThenZero.getMessage());
        verify(inventoryService, times(0)).addStock(-1, 5);
    }
}
