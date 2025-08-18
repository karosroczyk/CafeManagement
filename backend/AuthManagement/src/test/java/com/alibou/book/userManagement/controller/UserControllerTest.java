package com.alibou.book.userManagement.controller;

import com.alibou.book.exception.InvalidInputException;
import com.alibou.book.userManagement.entity.EmailRequest;
import com.alibou.book.userManagement.entity.User;
import com.alibou.book.userManagement.service.PaginatedResponse;
import com.alibou.book.userManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventory_ValidRequest() {
        // Arrange
        int page = 0;
        int size = 2;
        String[] sortBy = {"email"};
        String[] direction = {"id"};
        PaginatedResponse<User> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new User(), new User()), 0, 1, 2, 2);
        when(userService.getAllUsers(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<User>> response = userController.getAllUsers(page, size, sortBy, direction);

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
            userController.getAllUsers(page, size, sortBy, direction);
        });
    }

    @Test
    void testGetUserById_ValidRequest() {
        // Arrange
        Integer id = 1;
        User user = new User();
        when(userService.getUserById(id)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.getUserById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            userController.getUserById(id);
        });
    }

    @Test
    void testGetUserByEmail_ValidRequest() {
        // Arrange
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("test@gmail.com");
        User user = new User();
        when(userService.getUserByEmail(emailRequest.getEmail())).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.getUserByEmail(emailRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testCreateUser_ValidRequest() {
        // Arrange
        User user = new User();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(userService.createUser(user)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.createUser(user, result);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testCreateUser_InvalidRequest() {
        // Arrange
        User user = new User();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "user", "name", "Name is required"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            userController.createUser(user, result);
        });
    }

    @Test
    void testUpdateUser_ValidRequest() {
        // Arrange
        Integer id = 1;
        User user = new User();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(userService.updateUser(id, user)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.updateUser(id, user, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateUser_InvalidRequest() {
        // Arrange
        Integer id = -1;
        User user = new User();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            userController.updateUser(id, user, result);
        });
    }

    @Test
    void testDeleteUser_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(userService).deleteUser(id);

        // Act
        ResponseEntity<User> response = userController.deleteUser(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteUser_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            userController.deleteUser(id);
        });
    }
}
