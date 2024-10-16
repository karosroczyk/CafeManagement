package com.cafe.inventory.service;

import com.cafe.inventory.dao.InventoryDAOJPA;
import com.cafe.inventory.entity.InventoryItem;
import com.cafe.inventory.exception.DatabaseUniqueValidationException;
import com.cafe.inventory.exception.InvalidInputException;
import com.cafe.inventory.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryDAOJPA inventoryDAOJPA;
    private final WebClient webClient;
    @Value("${menu.service.url}")
    private String menuServiceUrl;

    public InventoryServiceImpl(InventoryDAOJPA inventoryDAOJPA, WebClient webClient) {
        this.inventoryDAOJPA = inventoryDAOJPA;
        this.webClient = webClient;
    }
    @Override
    public PaginatedResponse<InventoryItem> getAllInventoryItems(int page, int size, String[] sortBy, String[] direction) {
        List<Sort.Order> orders = IntStream.range(0, sortBy.length)
                .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(direction[i]), sortBy[i]))
                .toList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<InventoryItem> categoryPage = this.inventoryDAOJPA.findAll(pageable);

        return new PaginatedResponse<>(
                categoryPage.getContent(),
                categoryPage.getNumber(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements(),
                categoryPage.getSize());
    }

    @Override
    public InventoryItem getInventoryItemById(Integer id) {
        return this.inventoryDAOJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem with id: " + id + " not found."));
    }
    @Override
    public InventoryItem getInventoryItemByMenuItemId(Integer menuItemId) {
        return this.inventoryDAOJPA.findByMenuItemId(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem with id: " + menuItemId + " not found."));
    }

    @Override
    @Transactional
    public InventoryItem createInventoryItem(InventoryItem inventoryItem){
        try {
            return this.inventoryDAOJPA.save(inventoryItem);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public InventoryItem updateInventoryItem(Integer id, InventoryItem inventoryItem){
        InventoryItem foundInventoryItem = getInventoryItemById(id);

        foundInventoryItem.setMenuItemId(inventoryItem.getMenuItemId());
        foundInventoryItem.setStockLevel(inventoryItem.getStockLevel());
        foundInventoryItem.setAvailable(inventoryItem.isAvailable());

        try {
            return this.inventoryDAOJPA.save(foundInventoryItem);
        }catch(DataIntegrityViolationException e){
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteInventoryItem(Integer id) {
        getInventoryItemById(id);
        try {
            this.inventoryDAOJPA.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    public InventoryItem reduceStockByMenuItemId(Integer menuItemId, Integer quantity){
        InventoryItem foundInventoryItem = this.getInventoryItemByMenuItemId(menuItemId);
        Integer updatedStock = foundInventoryItem.getStockLevel() - quantity;

        if (updatedStock < 0)
            throw new InvalidInputException("Not enough stock to reduce.");

        if (updatedStock == 0) {
            foundInventoryItem.setAvailable(false);
            //updateMenuItemAvailability(id, false);
        }

        foundInventoryItem.setStockLevel(updatedStock);
        return this.inventoryDAOJPA.save(foundInventoryItem);
    }

    private void updateMenuItemAvailability(Integer menuItemId, Boolean availability) {
        String url = menuServiceUrl + "/api/menuitems/" + menuItemId + "/availability";

        try {
            webClient.patch()
                    .uri(url)
                    .bodyValue(availability)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        System.err.println("Client error: " + response.statusCode());
                        return Mono.error(new RuntimeException("Client error occurred"));
                    })
                    .onStatus(status -> status.is5xxServerError(), response -> {
                        System.err.println("Server error: " + response.statusCode());
                        return Mono.error(new RuntimeException("Server error occurred"));
                    })
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public InventoryItem addStock(Integer id, Integer quantity){
        InventoryItem foundInventoryItem = this.getInventoryItemById(id);
        Integer updatedStock = foundInventoryItem.getStockLevel() + quantity;

        if (updatedStock > 0)
            foundInventoryItem.setAvailable(true);

        foundInventoryItem.setStockLevel(updatedStock);
        return this.inventoryDAOJPA.save(foundInventoryItem);
    }
}
