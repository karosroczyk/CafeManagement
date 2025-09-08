package com.alibou.book.unit.roleManagement.controller;

import com.alibou.book.exception.InvalidInputException;
import com.alibou.book.roleManagement.controller.RoleController;
import com.alibou.book.roleManagement.entity.Role;
import com.alibou.book.roleManagement.service.PaginatedResponse;
import com.alibou.book.roleManagement.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

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
        PaginatedResponse<Role> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new Role(), new Role()), 0, 1, 2, 2);
        when(roleService.getAllRoles(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<Role>> response = roleController.getAllRoles(page, size, sortBy, direction);

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
            roleController.getAllRoles(page, size, sortBy, direction);
        });
    }

    @Test
    void testGetRoleById_ValidRequest() {
        // Arrange
        Integer id = 1;
        Role inventoryItem = new Role();
        when(roleService.getRoleById(id)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<Role> response = roleController.getRoleById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testGetRoleById_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            roleController.getRoleById(id);
        });
    }
    @Test
    void testCreateRole_ValidRequest() {
        // Arrange
        Role inventoryItem = new Role();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(roleService.createRole(inventoryItem)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<Role> response = roleController.createRole(inventoryItem, result);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testCreateRole_InvalidRequest() {
        // Arrange
        Role inventoryItem = new Role();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "inventoryItem", "name", "Name is required"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            roleController.createRole(inventoryItem, result);
        });
    }

    @Test
    void testUpdateRole_ValidRequest() {
        // Arrange
        Integer id = 1;
        Role inventoryItem = new Role();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(roleService.updateRole(id, inventoryItem)).thenReturn(inventoryItem);

        // Act
        ResponseEntity<Role> response = roleController.updateRole(id, inventoryItem, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
    }

    @Test
    void testUpdateRole_InvalidRequest() {
        // Arrange
        Integer id = -1;
        Role inventoryItem = new Role();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            roleController.updateRole(id, inventoryItem, result);
        });
    }

    @Test
    void testDeleteRole_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(roleService).deleteRole(id);

        // Act
        ResponseEntity<Role> response = roleController.deleteRole(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteRole_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            roleController.deleteRole(id);
        });
    }
}
