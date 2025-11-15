package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.dto.CategoryDTO;
import com.cafe.ordermanagement.entity.OrderItem;
import com.cafe.ordermanagement.exception.*;
import com.cafe.ordermanagement.dao.OrderDAOJPA;
import com.cafe.ordermanagement.entity.Order;
import com.netflix.discovery.EurekaClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private final OrderDAOJPA orderDAOJPA;
    @Autowired
    private WebClient.Builder webClientBuilder;
    private String menuServiceUrl;
    private String inventoryServiceUrl;
    @Autowired
    private EurekaClient discoveryClient;

    public OrderServiceImpl(OrderDAOJPA orderDAOJPA, WebClient.Builder webClientBuilder, EurekaClient discoveryClient){
        this.orderDAOJPA = orderDAOJPA;
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
        this.menuServiceUrl = discoveryClient.getNextServerFromEureka("menu", false).getHomePageUrl();
        this.inventoryServiceUrl = discoveryClient.getNextServerFromEureka("inventory", false).getHomePageUrl() + "/api/inventory";
    }

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

    public PaginatedResponse<MenuItemDTO> getAllMenuItems(int page, int size, String[] sortBy, String[] direction) {
        String uri = UriComponentsBuilder.fromHttpUrl(menuServiceUrl + "/api/menuitems")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sortBy", (Object[]) sortBy)
                .queryParam("direction", (Object[]) direction)
                .toUriString();

        return webClientBuilder.build().get()
            .uri(uri)
            .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);}))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ServerErrorException("Server error: " + errorBody);}))
            .bodyToMono(new ParameterizedTypeReference<PaginatedResponse<MenuItemDTO>>() {})
            .block();
    }

    @Override
    public PaginatedResponse<CategoryDTO> getAllMenuItemCategories(
            int page, int size, String[] sortBy, String[] direction) {
        String categoryUri = UriComponentsBuilder.fromHttpUrl(menuServiceUrl + "/api/categories")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sortBy", (Object[]) sortBy)
                .queryParam("direction", (Object[]) direction)
                .toUriString();

        return webClientBuilder.build().get()
                .uri(categoryUri)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);
                                }))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Server Error: " + errorBody);
                                    return Mono.error(new ServerErrorException("Server error: " + errorBody));
                                }))
                .bodyToMono(new ParameterizedTypeReference<PaginatedResponse<CategoryDTO>>() {
                })
                .block();
    }

    @Override
    public PaginatedResponse<MenuItemDTO> getAllMenuItemsByCategory(
            int page, int size, String[] sortBy, String[] direction, String categoryName){
        String uri = UriComponentsBuilder.fromHttpUrl(menuServiceUrl + "/api/menuitems/filter/category-name")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sortBy", (Object[]) sortBy)
                .queryParam("direction", (Object[]) direction)
                .queryParam("categoryName", categoryName)
                .toUriString();

        return webClientBuilder.build().get()
                .uri(uri)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);}))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ServerErrorException("Server error: " + errorBody);}))
                .bodyToMono(new ParameterizedTypeReference<PaginatedResponse<MenuItemDTO>>() {})
                .block();
    }

    @Override
    public Map<String, List<MenuItemDTO>> getMenuItemsGroupedByCategory(
            int page, int size, String[] sortBy, String[] direction){
        Map<String, List<MenuItemDTO>> categorizedMenuItems = new LinkedHashMap<>();
        List<CategoryDTO> categories =
                this.getAllMenuItemCategories(page, size, sortBy, direction).getData();

        categories.stream().forEach(
                category -> {categorizedMenuItems.put(
                        category.name(), this.getAllMenuItemsByCategory(page, size, sortBy, direction, category.name()).getData());});

        return categorizedMenuItems;
    }

    @Override
    @Transactional
    public Order placeOrder(Integer customerId,
                            List<Integer> menuItemIds,
                            List<Integer> quantitiesOfMenuItems) {

        List<Integer> filteredQuantities = quantitiesOfMenuItems.stream()
                .filter(quantity -> !quantity.equals(0))
                .collect(Collectors.toList());

        Order placedOrder = createOrder(new Order(customerId));
        menuItemIds.stream().forEach(menuItemId -> {
            OrderItem orderItem = new OrderItem(menuItemId, filteredQuantities.get(menuItemIds.indexOf(menuItemId)));
            placedOrder.addOrderItem(orderItem);
        });

        if (placedOrder.getOrderItems() == null || placedOrder.getOrderItems().isEmpty())
            throw new ResourceNotFoundException("Choose at least one Menu Item.");

        // Check if each selected MenuItem with choosen quantity is available
        List<Boolean> areMenuItemsAvailable = webClientBuilder.build().get()
                .uri(UriComponentsBuilder
                        .fromHttpUrl(inventoryServiceUrl + "/availability")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .queryParam("quantitiesOfMenuItems", filteredQuantities.toArray())
                        .toUriString())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);}))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ServerErrorException("Server error: " + errorBody);}))
                .bodyToMono(new ParameterizedTypeReference<List<Boolean>>() {})
                .block();

        IntStream.range(0, areMenuItemsAvailable.size())
                .filter(i -> !areMenuItemsAvailable.get(i))
                .findFirst()
                .ifPresent(i -> {
                    throw new ResourceNotFoundException("Menu item with ID: " + menuItemIds.get(i) + " is not available.");
                });

        // Reduce the stock for each selected MenuItem with choosen quantity
        webClientBuilder.build().put()
                .uri(UriComponentsBuilder
                        .fromHttpUrl(inventoryServiceUrl + "/reduce")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .queryParam("quantitiesOfMenuItems", filteredQuantities.toArray())
                        .toUriString())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);}))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ServerErrorException("Server error: " + errorBody);}))
                .bodyToMono(Void.class)
                .block();

        Map<Integer, Double> prices = webClientBuilder.build()
                .get()
                .uri(UriComponentsBuilder
                        .fromHttpUrl(menuServiceUrl + "/api/menuitems/prices")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .toUriString())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ClientErrorException("Client error: " + errorBody);}))
                .onStatus(
                        status -> status.is5xxServerError(), response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    throw new ServerErrorException("Server error: " + errorBody);}))
                .bodyToMono(new ParameterizedTypeReference<Map<Integer, Double>>() {})
                .block();

        // Calculate total price and update status
        double totalPrice = prices.values().stream()
                .mapToDouble(Double::doubleValue)
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
