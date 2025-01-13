package com.cafe.ordermanagement.controller;

import com.cafe.ordermanagement.dto.MenuItem;
import com.cafe.ordermanagement.entity.Category;
import com.cafe.ordermanagement.entity.Order;
import com.cafe.ordermanagement.exception.InvalidInputException;
import com.cafe.ordermanagement.service.OrderService;
import com.cafe.ordermanagement.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/ordersMVC")
public class OrderControllerMVC {
    private final OrderService orderService;
    private String inventoryServiceUrl;

    public OrderControllerMVC(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            Model model) {
        if (page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + " or size: " + size + " provided.");

        PaginatedResponse<Order> orders = this.orderService.getAllOrders(page, size, sortBy, direction);
        model.addAttribute("orders", orders.getData());
        model.addAttribute("currentPage", orders.getCurrentPage());
        model.addAttribute("totalPages", orders.getTotalPages());
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String getOrderById(@PathVariable Integer id, Model model) {
        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Order order = this.orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "orders/detail";
    }

    @GetMapping("/menuitemsByCategories")
    public String getAllMenuItemsByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            Model model) {
        if (page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + " or size: " + size + " provided.");

        Map<String, List<MenuItem>> categorizedMenuItems =
                this.orderService.getMenuItemsGroupedByCategory(page, size, sortBy, direction);

        model.addAttribute("categorizedMenuItems", categorizedMenuItems);
        return "orders/menuitems";
    }

    @GetMapping("/newOrder")
    public String showPlaceOrderForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            Model model) {
        if (page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + " or size: " + size + " provided.");

        Map<String, List<MenuItem>> categorizedMenuItems =
                this.orderService.getMenuItemsGroupedByCategory(page, size, sortBy, direction);

        model.addAttribute("newOrder", new Order());
        model.addAttribute("categorizedMenuItems", categorizedMenuItems);
        return "orders/create";
    }

    @PostMapping
    public String placeOrder(
            @Valid @ModelAttribute("order") Integer customerId,
            @RequestParam List<Integer> menuItemIds,
            @RequestParam List<Integer> quantitiesOfMenuItems,
            BindingResult result) {
        if (result.hasErrors())
            throw new InvalidInputException("Invalid Order provided: " + result.getFieldError().getDefaultMessage());

        Order placedOrder = this.orderService.placeOrder(customerId, menuItemIds, quantitiesOfMenuItems);
        return "redirect:/orders/" + placedOrder.getId();
    }

    @GetMapping("/edit/{id}")
    public String showUpdateOrderForm(@PathVariable Integer id, Model model) {
        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Order order = this.orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "orders/edit";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(
            @PathVariable Integer id,
            @Valid @ModelAttribute("order") Order order,
            BindingResult result) {
        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");
        if (result.hasErrors())
            throw new InvalidInputException("Invalid Order provided: " + result.getFieldError().getDefaultMessage());

        Order updatedOrder = this.orderService.updateOrder(id, order);
        return "redirect:/orders/" + updatedOrder.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}
