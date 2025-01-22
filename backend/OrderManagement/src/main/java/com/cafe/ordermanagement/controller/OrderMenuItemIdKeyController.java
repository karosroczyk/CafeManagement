package com.cafe.ordermanagement.controller;

import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.entity.OrderMenuItemId;
import com.cafe.ordermanagement.entity.OrderMenuItemIdKey;
import com.cafe.ordermanagement.exception.InvalidInputException;
import com.cafe.ordermanagement.service.OrderMenuItemIdKeyService;
import com.cafe.ordermanagement.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orderMenuItemIdKeys")
public class OrderMenuItemIdKeyController {
    private final OrderMenuItemIdKeyService orderMenuItemIdKeyService;
    public OrderMenuItemIdKeyController(OrderMenuItemIdKeyService orderMenuItemIdKeyService){
        this.orderMenuItemIdKeyService = orderMenuItemIdKeyService;
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<List<OrderMenuItemId>> getOrderMenuItemIdKeyByOrderId(@PathVariable Integer order_id){
        if(order_id < 0)
            throw new InvalidInputException("Invalid customer id: " + order_id + " provided.");

        List<OrderMenuItemId> orders = this.orderMenuItemIdKeyService.getOrderMenuItemIdKeyByOrderId(order_id);
        return ResponseEntity.ok(orders);
    }
}
