package com.cafe.ordermanagement.controller;

import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.dto.PlaceOrderRequest;
import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.exception.InvalidInputException;
import com.cafe.ordermanagement.service.OrderService;
import com.cafe.ordermanagement.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }
    @GetMapping
    public ResponseEntity<PaginatedResponse<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "customerId") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction){
        if(page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<Order> orders = this.orderService.getAllOrders(page, size, sortBy, direction);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Order order = this.orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{customer_id}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Integer customer_id){
        if(customer_id < 0)
            throw new InvalidInputException("Invalid customer id: " + customer_id + " provided.");

        List<Order> orders = this.orderService.getOrdersByCustomerId(customer_id);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());

        Order createdOrder = this.orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PostMapping("/items")
    public ResponseEntity<Order> placeOrder(
            @Valid @RequestBody PlaceOrderRequest orderRequest, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());

        if(orderRequest.menuItemIds().size() != orderRequest.quantitiesOfMenuItems().size())
            throw new InvalidInputException("All choosen MenuItems must have corresponding quantities.");

        Order createdOrder =
                this.orderService.placeOrder(orderRequest.customerId(), orderRequest.menuItemIds(), orderRequest.quantitiesOfMenuItems());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Integer id, @Valid @RequestBody Order order, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException("Invalid Order: " + result.getFieldError().getDefaultMessage() + " provided.");

        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Order updatedOrder = this.orderService.updateOrder(id, order);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
