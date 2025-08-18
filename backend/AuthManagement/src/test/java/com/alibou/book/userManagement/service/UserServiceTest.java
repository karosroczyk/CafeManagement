package com.alibou.book.userManagement.service;

import com.alibou.book.exception.DatabaseUniqueValidationException;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.userManagement.dao.UserDAOJPA;
import com.alibou.book.userManagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserDAOJPA userDAOJPA;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllUsers() {
        User item1 = new User();
        User item2 = new User();
        item2.setEmail("test@gmail.com");
        String[] sortingFields = {"id", "first_name"};
        String[] directions = {"asc", "desc"};

        List<Sort.Order> orders = Arrays.asList(
                new Sort.Order(Sort.Direction.ASC, "id"),
                new Sort.Order(Sort.Direction.DESC, "first_name")
        );
        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<User> userPage = new PageImpl<>(Arrays.asList(item1, item2));

        when(userDAOJPA.findAll(pageable)).thenReturn(userPage);
        PaginatedResponse<User> result = userService.getAllUsers(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals(Arrays.asList(item1, item2), result.getData());
        verify(userDAOJPA, times(1)).findAll(pageable);
    }

    @Test
    void testGetUserById_Correct() {
        User item = new User();
        item.setId(1);
        when(userDAOJPA.findById(1)).thenReturn(Optional.of(item));
        User result = userService.getUserById(1);

        assertEquals(1, result.getId());
        verify(userDAOJPA, times(1)).findById(1);
    }

    @Test
    void testGetUserById_IdNotFound() {
        when(userDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(100)
        );

        assertEquals("User with id: 100 not found.", exception.getMessage());
        verify(userDAOJPA, times(1)).findById(100);
    }

    @Test
    void testGetUserByEmail_Correct() {
        User item = new User();
        item.setEmail("test@gmail.com");
        when(userDAOJPA.findByEmail("test@gmail.com")).thenReturn(Optional.of(item));
        User result = userService.getUserByEmail("test@gmail.com");

        assertEquals("test@gmail.com", result.getEmail());
        verify(userDAOJPA, times(0)).findById(1);
        verify(userDAOJPA, times(1)).findByEmail("test@gmail.com");
    }
    
    @Test
    void testCreateUser() {
        User item = new User();
        item.setId(1);

        when(userDAOJPA.save(item)).thenReturn(item);
        User result = userService.createUser(item);

        assertEquals(1, result.getId());
        verify(userDAOJPA, times(1)).save(item);
    }

    @Test
    void testUpdateUser_Correct() {
        User existingItem = new User();
        existingItem.setId(1);
        existingItem.setPassword("Password");
        User updatedItem = new User();
        updatedItem.setId(1);
        updatedItem.setPassword("newPassword");
        updatedItem.setEmail("test@gmail.com");

        when(userDAOJPA.findById(1)).thenReturn(Optional.of(existingItem));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userDAOJPA.save(any(User.class))).thenReturn(updatedItem);
        User result = userService.updateUser(1, updatedItem);

        assertEquals("test@gmail.com", result.getEmail());
        verify(userDAOJPA, times(1)).findById(1);
    }

    @Test
    void testDeleteUser_Correct() {
        User item = new User();

        when(userDAOJPA.findById(1)).thenReturn(Optional.of(item));
        doNothing().when(userDAOJPA).deleteById(1);

        userService.deleteUser(1);

        verify(userDAOJPA, times(1)).findById(1);
        verify(userDAOJPA, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUser_IdNotFound() {
        when(userDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(100)
        );

        assertEquals("User with id: 100 not found.", exception.getMessage());
        verify(userDAOJPA, times(1)).findById(100);
    }
}
