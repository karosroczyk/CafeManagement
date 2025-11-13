package com.cafe.ordermanagement.integration;

import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.exception.InvalidInputException;
import com.cafe.ordermanagement.service.PaginatedResponse;
import com.cafe.ordermanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(OrderManagementApplicationTests.TestConfig.class)
class OrderManagementApplicationTests {
    @TestConfiguration
    static class TestConfig {

        @Bean
        public EurekaClient eurekaClient() {
            EurekaClient mockClient = Mockito.mock(EurekaClient.class);
            InstanceInfo menuInstance = InstanceInfo.Builder.newBuilder()
                    .setAppName("menu")
                    .setHomePageUrl("http://localhost:8081", null)
                    .build();
            InstanceInfo inventoryInstance = InstanceInfo.Builder.newBuilder()
                    .setAppName("inventory")
                    .setHomePageUrl("http://localhost:8082", null)
                    .build();

            Mockito.when(mockClient.getNextServerFromEureka("menu", false))
                    .thenReturn(menuInstance);
            Mockito.when(mockClient.getNextServerFromEureka("inventory", false))
                    .thenReturn(inventoryInstance);

            return mockClient;
        }

        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOrders_returnsPage() throws Exception {
        Order order1 = new Order(10);
        order1.setId(1);
        Order order2 = new Order(15);
        order2.setId(2);

        PaginatedResponse<Order> response =
                new PaginatedResponse<>(List.of(order1, order2), 0, 1, 1, 1);

        when(orderService.getAllOrders(eq(0), eq(5), any(), any())).thenReturn(response);

        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "customerId")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].customerId").value(10));
    }

    @Test
    void getOrderById_returnsOrder() throws Exception {
        Order order = new Order(1);
        order.setId(1);

        when(orderService.getOrderById(1)).thenReturn(order);

        String response = mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Order returnedOrder = new ObjectMapper().readValue(response, Order.class);

        assertThat(returnedOrder.getId()).isEqualTo(1);
        assertThat(returnedOrder.getCustomerId()).isEqualTo(1);
    }

    @Test
    void getOrderByCustomerId_returnsOrder() throws Exception {
        Order order1 = new Order(1);
        order1.setId(1);
        Order order2 = new Order(1);
        order2.setId(2);

        when(orderService.getOrdersByCustomerId(1)).thenReturn(Arrays.asList(order1, order2));


        String response = mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Order> returnedOrders = new ObjectMapper().readValue(response, new TypeReference<List<Order>>() {});

        assertThat(returnedOrders).hasSize(2);
        assertThat(returnedOrders.get(0).getId()).isEqualTo(1);
        assertThat(returnedOrders.get(0).getCustomerId()).isEqualTo(1);
        assertThat(returnedOrders.get(1).getId()).isEqualTo(2);
        assertThat(returnedOrders.get(1).getCustomerId()).isEqualTo(1);
    }

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {
        Order order = new Order(20);
        order.setId(200);

        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.customerId").value(20));
    }

    @Test
    void placeOrder_createsOrder() throws Exception {
        Integer customerId = 1;
        List<Integer> menuItemIds = List.of(10, 20);
        List<Integer> quantities = List.of(2, 3);

        Order createdOrder = new Order(customerId);
        createdOrder.setId(100);

        when(orderService.placeOrder(customerId, menuItemIds, quantities)).thenReturn(createdOrder);

        mockMvc.perform(post("/orders/placeOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerId))
                        .param("menuItemIds", "10", "20")
                        .param("quantitiesOfMenuItems", "2", "3"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void updateOrder_returnsUpdatedOrder() throws Exception {
        Order order = new Order(5);
        order.setId(1);

        Order updatedOrder = new Order(10);
        updatedOrder.setId(1);

        when(orderService.updateOrder(eq(1), any(Order.class))).thenReturn(updatedOrder);

        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(10));
    }

    @Test
    void updateOrder_withNegativeId_returnsBadRequest() throws Exception {
        Order order = new Order(5);
        order.setId(1);

        mockMvc.perform(put("/orders/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(InvalidInputException.class)
                                .hasMessageContaining("Invalid id"));
    }

    @Test
    void deleteOrder_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/orders/5"))
                .andExpect(status().isNoContent());
    }
}
