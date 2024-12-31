package com.cafe.ordermanagement.controller;

import com.cafe.ordermanagement.dto.MenuItem;
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
@RequestMapping("/ordersREST")
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

    @GetMapping("/menuitems")
    public ResponseEntity<PaginatedResponse<MenuItem>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "customerId") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction
    ) {
        PaginatedResponse<MenuItem> menuitems = orderService.getAllMenuItems(page, size, sortBy, direction);
        return ResponseEntity.ok(menuitems);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());

        Order createdOrder = this.orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody Order order,
                                            @RequestParam List<Integer> menuItemIds,
                                            @RequestParam List<Integer> quantitiesOfMenuItems,
                                            BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());

        Order createdOrder = this.orderService.placeOrder(order, menuItemIds, quantitiesOfMenuItems);
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
    public ResponseEntity<Order> deleteOrder(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
