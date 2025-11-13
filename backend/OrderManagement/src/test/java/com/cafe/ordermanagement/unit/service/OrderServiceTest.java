package com.cafe.ordermanagement.unit.service;

import com.cafe.ordermanagement.dao.OrderDAOJPA;
import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.exception.DatabaseUniqueValidationException;
import com.cafe.ordermanagement.exception.ResourceNotFoundException;
import com.cafe.ordermanagement.service.OrderServiceImpl;
import com.cafe.ordermanagement.service.PaginatedResponse;
import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderDAOJPA orderDAOJPA;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private EurekaClient discoveryClient;
    private OrderServiceImpl orderService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(discoveryClient.getNextServerFromEureka(eq("menu"), anyBoolean()))
                .thenReturn(com.netflix.appinfo.InstanceInfo.Builder.newBuilder().setAppName("menu").build());
        when(discoveryClient.getNextServerFromEureka(eq("inventory"), anyBoolean()))
                .thenReturn(com.netflix.appinfo.InstanceInfo.Builder.newBuilder().setAppName("inventory").build());

        orderService = new OrderServiceImpl(orderDAOJPA, webClientBuilder, discoveryClient);
    }

    @Test
    void testGetAllOrders() {
        Order order1 = new Order("PENDING", 2.5, 1);
        Order order2 = new Order("PENDING", 5.5, 2);
        String[] sortingFields = {"total_price", "customerId"};
        String[] directions = {"asc", "desc"};

        List<Sort.Order> orders = Arrays.asList(
                new Sort.Order(Sort.Direction.ASC, "total_price"),
                new Sort.Order(Sort.Direction.DESC, "customerId")
        );
        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order1, order2));

        when(orderDAOJPA.findAll(pageable)).thenReturn(orderPage);
        PaginatedResponse<Order> result = orderService.getAllOrders(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals(2.5, result.getData().get(0).getTotal_price());
        verify(orderDAOJPA, times(1)).findAll(pageable);
    }

    @Test
    void testGetOrderById_Correct() {
        Order order = new Order("PENDING", 2.5, 1);

        when(orderDAOJPA.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);

        assertEquals("PENDING", result.getStatus());
        assertEquals(2.5, result.getTotal_price());
        assertEquals(1, result.getCustomerId());
        verify(orderDAOJPA, times(1)).findById(1);
    }

    @Test
    void testGetOrderById_IdNotFound() {
        when(orderDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById(100)
        );

        assertEquals("Order with id: 100 not found.", exception.getMessage());
        verify(orderDAOJPA, times(1)).findById(100);
    }

    @Test
    void testGetOrdersByCustomerId_Correct() {
        Order order1 = new Order("PENDING", 2.5, 1);
        Order order2 = new Order("PENDING", 2.5, 1);
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderDAOJPA.findOrdersByCustomerId(1)).thenReturn(Optional.of(orders));

        List<Order> result = orderService.getOrdersByCustomerId(1);

        assertEquals(2, result.size());
        verify(orderDAOJPA, times(1)).findOrdersByCustomerId(1);
    }

    @Test
    void testGetOrdersByCustomerId_IdNotFound() {
        when(orderDAOJPA.findOrdersByCustomerId(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrdersByCustomerId(100)
        );

        assertEquals("Customer with id: 100 not found.", exception.getMessage());
        verify(orderDAOJPA, times(1)).findOrdersByCustomerId(100);
    }

    @Test
    void testCreateInventoryItem() {
        Order item = new Order("PENDING", 2.5, 1);

        when(orderDAOJPA.save(item)).thenReturn(item);
        Order result = orderService.createOrder(item);

        assertEquals("PENDING", result.getStatus());
        verify(orderDAOJPA, times(1)).save(item);
    }

    @Test
    void testCreateInventoryItem_DuplicateMenuItemId() {
        Order item = new Order("PENDING", 2.5, 1);
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Duplicate entry");

        when(orderDAOJPA.save(item)).thenThrow(exception);

        DatabaseUniqueValidationException thrownException = assertThrows(
                DatabaseUniqueValidationException.class,
                () -> orderService.createOrder(item)
        );

        assertEquals("Duplicate entry", thrownException.getMessage());
        verify(orderDAOJPA, times(1)).save(item);
    }

    @Test
    void testUpdateOrder() {
        Order order = new Order("PENDING", 2.5, 1);
        order.setId(1);
        Order orderUpdated = new Order("COMPLETED", 25.5, 2);
        orderUpdated.setId(1);

        when(orderDAOJPA.findById(1)).thenReturn(Optional.of(order));
        when(orderDAOJPA.save(any(Order.class))).thenReturn(orderUpdated);
        Order result = orderService.updateOrder(1, orderUpdated);

        assertEquals("COMPLETED", result.getStatus());
        assertEquals(25.5, result.getTotal_price());
        assertEquals(2, result.getCustomerId());
        verify(orderDAOJPA, times(1)).findById(1);
    }

    @Test
    void testUpdateOrder_ShouldThrowDatabaseUniqueValidationException_WhenSaveFails() {
        Order existingOrder = new Order("COMPLETED", 25.5, 2);
        when(orderDAOJPA.findById(1)).thenReturn(Optional.of(existingOrder));
        when(orderDAOJPA.save(any(Order.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate", new RuntimeException("Duplicate entry")));

        DatabaseUniqueValidationException ex = assertThrows(
                DatabaseUniqueValidationException.class,
                () -> orderService.updateOrder(1, new Order())
        );
        assertTrue(ex.getMessage().contains("Duplicate entry"));
    }

    @Test
    void testDeleteInventoryItem_Correct() {
        Order item = new Order("COMPLETED", 25.5, 2);

        when(orderDAOJPA.findById(1)).thenReturn(Optional.of(item));
        doNothing().when(orderDAOJPA).deleteById(1);

        orderService.deleteOrder(1);

        verify(orderDAOJPA, times(1)).findById(1);
        verify(orderDAOJPA, times(1)).deleteById(1);
    }

    @Test
    void testDeleteOrder_IdNotFound() {
        when(orderDAOJPA.findById(100)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.deleteOrder(100)
        );

        assertEquals("Order with id: 100 not found.", exception.getMessage());
        verify(orderDAOJPA, times(1)).findById(100);
    }
}
