package com.cafe.ordermanagement.unit.controller;

import com.cafe.ordermanagement.controller.OrderController;
import com.cafe.ordermanagement.dto.PlaceOrderRequest;
import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.exception.InvalidInputException;
import com.cafe.ordermanagement.service.OrderService;
import com.cafe.ordermanagement.service.PaginatedResponse;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrders_ValidRequest() {
        // Arrange
        int page = 0;
        int size = 2;
        String[] sortBy = {"customerId"};
        String[] direction = {"asc"};
        PaginatedResponse<Order> paginatedResponse = new PaginatedResponse<>(
                Arrays.asList(new Order(), new Order()), 0, 1, 2, 2);
        when(orderService.getAllOrders(page, size, sortBy, direction)).thenReturn(paginatedResponse);

        // Act
        ResponseEntity<PaginatedResponse<Order>> response = orderController.getAllOrders(page, size, sortBy, direction);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void testGetAllOrders_InvalidRequest() {
        // Arrange
        int page = -1;
        int size = 0;
        String[] sortBy = {"customerId"};
        String[] direction = {"asc"};

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            orderController.getAllOrders(page, size, sortBy, direction);
        });
    }

    @Test
    void testGetOrderById_ValidRequest() {
        // Arrange
        Integer id = 1;
        Order order = new Order();
        when(orderService.getOrderById(id)).thenReturn(order);

        // Act
        ResponseEntity<Order> response = orderController.getOrderById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    void testGetOrderById_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            orderController.getOrderById(id);
        });
    }

    @Test
    void testGetOrdersByCustomerId() {
        // Arrange
        Integer id = 1;
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderService.getOrdersByCustomerId(id)).thenReturn(orders);

        // Act
        ResponseEntity<List<Order>> response = orderController.getOrdersByCustomerId(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    void testCreateOrder_ValidRequest() {
        // Arrange
        Order orderItem = new Order();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(orderService.createOrder(orderItem)).thenReturn(orderItem);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(orderItem, result);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderItem, response.getBody());
    }

    @Test
    void testCreateOrder_InvalidRequest() {
        // Arrange
        Order orderItem = new Order();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "orderItem", "name", "Name is required"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            orderController.createOrder(orderItem, result);
        });
    }

    @Test
    void testPlaceOrder_Success() {
        Integer customerId = 1;
        List<Integer> menuItemIds = List.of(1, 2);
        List<Integer> quantities = List.of(1, 3);
        Order createdOrder = new Order("PENDING", 30.0, customerId);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(orderService.placeOrder(customerId, menuItemIds, quantities)).thenReturn(createdOrder);

        ResponseEntity<Order> response = orderController.placeOrder(new PlaceOrderRequest(customerId, menuItemIds, quantities), bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdOrder, response.getBody());
        verify(orderService).placeOrder(customerId, menuItemIds, quantities);
    }

    @Test
    void testPlaceOrder_InvalidInput() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new org.springframework.validation.FieldError(
                "order", "customerId", "CustomerId required"));

        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> orderController.placeOrder(new PlaceOrderRequest(1, List.of(1), List.of(1)), bindingResult));

        assertEquals("CustomerId required", ex.getMessage());
        verifyNoInteractions(orderService);
    }

    @Test
    void testUpdateOrder_ValidRequest() {
        // Arrange
        Integer id = 1;
        Order orderItem = new Order();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(orderService.updateOrder(id, orderItem)).thenReturn(orderItem);

        // Act
        ResponseEntity<Order> response = orderController.updateOrder(id, orderItem, result);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderItem, response.getBody());
    }

    @Test
    void testUpdateOrder_InvalidRequest() {
        // Arrange
        Integer id = -1;
        Order orderItem = new Order();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            orderController.updateOrder(id, orderItem, result);
        });
    }

    @Test
    void testDeleteOrder_ValidRequest() {
        // Arrange
        Integer id = 1;
        doNothing().when(orderService).deleteOrder(id);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteOrder_InvalidRequest() {
        // Arrange
        Integer id = -1;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            orderController.deleteOrder(id);
        });
    }
}
