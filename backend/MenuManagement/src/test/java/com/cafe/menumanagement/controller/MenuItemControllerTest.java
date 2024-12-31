package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.MenuItemService;
import com.cafe.menumanagement.service.PaginatedResponse;
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

public class MenuItemControllerTest {
    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMenuItems_ValidRequest() {
        // Arrange
        int page = 0;
        int size = 2;
        String[] sortBy = {"name"};
        String[] direction = {"asc"};
        PaginatedResponse<MenuItem> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new MenuItem(), new MenuItem()), 0, 1, 2, 2);
        when(menuItemService.getAllMenuItems(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<MenuItem>> response = menuItemController.getAllMenuItems(page, size, sortBy, direction);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void testGetAllMenuItems_InvalidRequest() {
        // Arrange
        int page = -1;
        int size = 0;
        String[] sortBy = {"name"};
        String[] direction = {"asc"};

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.getAllMenuItems(page, size, sortBy, direction);
        });
    }

    @Test
    void testGetMenuItemsByCategoryName_ValidRequest() {
        // Arrange
        String categoryName = "Beverages";
        int page = 0;
        int size = 2;
        String[] sortBy = {"name"};
        String[] direction = {"asc"};
        PaginatedResponse<MenuItem> paginatedResponse = new PaginatedResponse<>(
                Collections.singletonList(new MenuItem()), 0, 1, 1, 2);
        when(menuItemService.getMenuItemsByCategoryName(categoryName, page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<MenuItem>> response = menuItemController.getMenuItemsByCategoryName(page, size, sortBy, direction, categoryName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetMenuItemById_ValidRequest() {
        // Arrange
        Integer id = 1;
        MenuItem menuItem = new MenuItem();
        when(menuItemService.getMenuItemById(id)).thenReturn(menuItem);

        // Act
        ResponseEntity<MenuItem> response = menuItemController.getMenuItemById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItem, response.getBody());
    }

    @Test
    void testGetMenuItemById_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.getMenuItemById(id);
        });
    }

    @Test
    void testCreateMenuItem_ValidRequest() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(menuItemService.createMenuItem(menuItem)).thenReturn(menuItem);

        // Act
        ResponseEntity<MenuItem> response = menuItemController.createMenuItem(menuItem, result);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(menuItem, response.getBody());
    }

    @Test
    void testCreateMenuItem_InvalidRequest() {
        // Arrange
        MenuItem menuItem = new MenuItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "menuItem", "name", "Name is required"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.createMenuItem(menuItem, result);
        });
    }

    @Test
    void testUpdateMenuItem_ValidRequest() {
        // Arrange
        Integer id = 1;
        MenuItem menuItem = new MenuItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(menuItemService.updateMenuItem(id, menuItem)).thenReturn(menuItem);

        // Act
        ResponseEntity<MenuItem> response = menuItemController.updateMenuItem(id, menuItem, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menuItem, response.getBody());
    }

    @Test
    void testUpdateMenuItem_InvalidRequest() {
        // Arrange
        Integer id = -1;
        MenuItem menuItem = new MenuItem();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.updateMenuItem(id, menuItem, result);
        });
    }

    @Test
    void testDeleteMenuItem_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(menuItemService).deleteMenuItem(id);

        // Act
        ResponseEntity<MenuItem> response = menuItemController.deleteMenuItem(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteMenuItem_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.deleteMenuItem(id);
        });
    }
}
