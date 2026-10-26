package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.config.InventoryClient;
import com.cafe.ordermanagement.config.MenuClient;
import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.dto.CategoryDTO;
import com.cafe.ordermanagement.entity.OrderItem;
import com.cafe.ordermanagement.exception.*;
import com.cafe.ordermanagement.dao.OrderDAOJPA;
import com.cafe.ordermanagement.entity.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    @Autowired
    private final OrderDAOJPA orderDAOJPA;
    private final MenuClient menuClient;
    private final InventoryClient inventoryClient;
    @Override
    public PaginatedResponse<Order> getAllOrders(int page, int size, String[] sortBy, String[] direction) {
        List<Sort.Order> orders = IntStream.range(0, sortBy.length)
                .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(direction[i]), sortBy[i]))
                .toList();
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Order> ordersPage = this.orderDAOJPA.findAll(pageable);

        return new PaginatedResponse<>(
                ordersPage.getContent(),
                ordersPage.getNumber(),
                ordersPage.getTotalPages(),
                ordersPage.getTotalElements(),
                ordersPage.getSize());
    }

    @Override
    public Order getOrderById(Integer id) {
        return this.orderDAOJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id: " + id + " not found."));
    }

    @Override
    public List<Order> getOrdersByCustomerId(Integer customer_id) {
        return this.orderDAOJPA.findOrdersByCustomerId(customer_id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + customer_id + " not found."));
    }

    @Override
    public Map<String, List<MenuItemDTO>> getMenuItemsGroupedByCategory(
            int page, int size, String[] sortBy, String[] direction){
        Map<String, List<MenuItemDTO>> categorizedMenuItems = new LinkedHashMap<>();
        List<CategoryDTO> categories =
                menuClient.getAllMenuItemCategories(page, size, sortBy, direction).getData();

        categories.stream().forEach(
                category -> {categorizedMenuItems.put(
                        category.name(), menuClient.getAllMenuItemsByCategory(page, size, sortBy, direction, category.name()).getData());});

        return categorizedMenuItems;
    }

    @Override
    @Transactional
    public Order placeOrder(Integer customerId,
                            List<Integer> menuItemIds,
                            List<Integer> quantitiesOfMenuItems) {

        Order placedOrder = createOrder(new Order(customerId));
        IntStream.range(0, menuItemIds.size())
                .forEach(i -> {
                    OrderItem orderItem = new OrderItem(menuItemIds.get(i), quantitiesOfMenuItems.get(i));
                    placedOrder.addOrderItem(orderItem);
                });

        if (placedOrder.getOrderItems() == null || placedOrder.getOrderItems().isEmpty())
            throw new ResourceNotFoundException("Choose at least one Menu Item.");

        List<Boolean> areMenuItemsAvailable = inventoryClient.checkAvailability(menuItemIds, quantitiesOfMenuItems);

        IntStream.range(0, areMenuItemsAvailable.size())
                .filter(i -> !areMenuItemsAvailable.get(i))
                .findFirst()
                .ifPresent(i -> {
                    throw new ResourceNotFoundException("Menu item with ID: " + menuItemIds.get(i) + " is not available.");
                });

        // Reduce the stock for each selected MenuItem with choosen quantity
        List<Integer> negativeQuantities = quantitiesOfMenuItems.stream()
                .map(quantity -> quantity*(-1))
                        .collect(Collectors.toList());

        inventoryClient.decreaseStock(menuItemIds, negativeQuantities);

        List<Double> priceList = new ArrayList<>(menuClient.getPrices(menuItemIds).values());
        double totalPrice = IntStream.range(0, priceList.size())
                .mapToDouble(i -> priceList.get(i) * quantitiesOfMenuItems.get(i))
                .sum();

        placedOrder.setTotal_price(totalPrice);
        placedOrder.setStatus("PENDING");

        return placedOrder;
    }
    @Override
    @Transactional
    public Order createOrder(Order menuItem){
        try {
            return this.orderDAOJPA.save(menuItem);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getMessage());
        }
    }
    @Override
    @Transactional
    public Order updateOrder(Integer id, Order order){
        Order foundOrder = getOrderById(id);

        foundOrder.setStatus(order.getStatus());
        foundOrder.setTotal_price(order.getTotal_price());
        foundOrder.setCustomerId(order.getCustomerId());

        try {
            return this.orderDAOJPA.save(foundOrder);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        getOrderById(id);
        try {
            this.orderDAOJPA.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }
}
