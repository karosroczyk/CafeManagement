package com.cafe.menumanagement.unit.controller;

import com.cafe.menumanagement.controller.MenuItemController;
import com.cafe.menumanagement.dto.MenuItemDTO;
import com.cafe.menumanagement.dto.MenuItemMapper;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.CategoryService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuItemControllerTest {
    @Mock
    private MenuItemService menuItemService;
    @Mock
    private CategoryService categoryService;
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
        String categoryName = null;
        PaginatedResponse<MenuItem> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new MenuItem(), new MenuItem()), 0, 1, 2, 2);
        when(menuItemService.getAllMenuItems(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<MenuItemDTO>> response =
                menuItemController.getAllMenuItems(page, size, sortBy, direction, categoryName);

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
        String categoryName = null;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            menuItemController.getAllMenuItems(page, size, sortBy, direction, categoryName);
        });
    }

    @Test
    void testGetMenuItemById_ValidRequest() {
        // Arrange
        Integer id = 1;
        MenuItem menuItem = new MenuItem();
        when(menuItemService.getMenuItemById(id)).thenReturn(menuItem);

        // Act
        ResponseEntity<MenuItemDTO> response = menuItemController.getMenuItemById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MenuItemMapper.toDTO(menuItem), response.getBody());
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
        ResponseEntity<PaginatedResponse<MenuItemDTO>> response =
                menuItemController.getAllMenuItems(page, size, sortBy, direction, categoryName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testCreateMenuItem_ValidRequest() {
        Category category = new Category();
        category.setId(1);
        category.setName("Coffee");

        MenuItem menuItem = new MenuItem("Espresso", "Strong coffee", 5.0, category, null);
        MenuItemDTO menuItemDTO = MenuItemMapper.toDTO(menuItem);

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(categoryService.getCategoryById(menuItemDTO.categoryId())).thenReturn(category);
        when(menuItemService.createMenuItem(any(MenuItem.class))).thenReturn(menuItem);

        ResponseEntity<MenuItemDTO> response = menuItemController.createMenuItem(menuItemDTO, result);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(menuItem.getName(), response.getBody().name());
        assertEquals(menuItem.getDescription(), response.getBody().description());
        assertEquals(menuItem.getPrice(), response.getBody().price());
        assertEquals(menuItem.getCategory().getId(), response.getBody().categoryId());
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
            menuItemController.createMenuItem(MenuItemMapper.toDTO(menuItem), result);
        });
    }

    @Test
    void testUpdateMenuItem_ValidRequest() {
        // Arrange
        Integer id = 1;

        // Mock category
        Category category = new Category();
        category.setId(1);
        category.setName("Desserts");

        // Mock existing and updated menu item
        MenuItem existingMenuItem = new MenuItem("Old Cake", "Chocolate cake", 8.0, category, null);
        MenuItem updatedMenuItem = new MenuItem("Cheesecake", "Creamy cheesecake", 9.0, category, null);
        updatedMenuItem.setId(id);

        // Map to DTO
        MenuItemDTO updatedDTO = MenuItemMapper.toDTO(updatedMenuItem);

        // Mock validation result
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Mock dependencies
        when(categoryService.getCategoryById(updatedDTO.categoryId())).thenReturn(category);
        when(menuItemService.updateMenuItem(eq(id), any(MenuItem.class))).thenReturn(updatedMenuItem);

        // Act
        ResponseEntity<MenuItemDTO> response = menuItemController.updateMenuItem(id, updatedDTO, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedMenuItem.getId(), response.getBody().id());
        assertEquals(updatedMenuItem.getName(), response.getBody().name());
        assertEquals(updatedMenuItem.getDescription(), response.getBody().description());
        assertEquals(updatedMenuItem.getPrice(), response.getBody().price());
        assertEquals(updatedMenuItem.getCategory().getId(), response.getBody().categoryId());
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
            menuItemController.updateMenuItem(id, MenuItemMapper.toDTO(menuItem), result);
        });
    }

    @Test
    void testDeleteMenuItem_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(menuItemService).deleteMenuItem(id);

        // Act
        ResponseEntity<Void> response = menuItemController.deleteMenuItem(id);

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
