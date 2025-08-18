package com.cafe.inventory.controller;

import com.cafe.inventory.entity.InventoryItem;
import com.cafe.inventory.exception.InvalidInputException;
import com.cafe.inventory.service.InventoryService;
import com.cafe.inventory.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<InventoryItem>> getAllInventoryItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "menuItemId") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction){
        if(page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<InventoryItem> inventory = this.inventoryService.getAllInventoryItems(page, size, sortBy, direction);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        InventoryItem inventoryItem = this.inventoryService.getInventoryItemById(id);
        return ResponseEntity.ok(inventoryItem);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<Boolean>> getInventoryItemAvailabilityByMenuItemIds
            (@RequestParam List<Integer> menuItemIds, @RequestParam List<Integer> quantitiesOfMenuItems){
        if (menuItemIds == null || menuItemIds.isEmpty() ||
                quantitiesOfMenuItems == null || quantitiesOfMenuItems.isEmpty())
            throw new InvalidInputException("Invalid id or quantity provided.");

        List<Boolean> inventoryItems =
                this.inventoryService.areInventoryItemsByMenuItemIdsAvailable(menuItemIds, quantitiesOfMenuItems);
        return ResponseEntity.ok(inventoryItems);
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(@Valid @RequestBody InventoryItem inventoryItem, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());

        InventoryItem createdInventoryItem = this.inventoryService.createInventoryItem(inventoryItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInventoryItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateInventoryItem(
            @PathVariable Integer id, @Valid @RequestBody InventoryItem inventoryItem, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException("Invalid Inventory Item: " + result.getFieldError().getDefaultMessage() + " provided.");

        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        InventoryItem updatedInventoryItem = this.inventoryService.updateInventoryItem(id, inventoryItem);
        return ResponseEntity.ok(updatedInventoryItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InventoryItem> deleteInventoryItem(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.inventoryService.deleteInventoryItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/reduce")
    public ResponseEntity<List<InventoryItem>> reduceStockByMenuItemId(
            @RequestParam List<Integer> menuItemIds, @RequestParam List<Integer> quantitiesOfMenuItems){
        if (menuItemIds == null || menuItemIds.isEmpty() || quantitiesOfMenuItems == null || quantitiesOfMenuItems.isEmpty())
            throw new InvalidInputException("Invalid id or quantity provided.");

        List<InventoryItem> updatedInventoryItem = this.inventoryService.reduceStockByMenuItemId(
                menuItemIds, quantitiesOfMenuItems);
        return ResponseEntity.ok(updatedInventoryItem);
    }

    @PutMapping("/{id}/add")
    public ResponseEntity<InventoryItem> addStock(
            @PathVariable Integer id, @RequestBody Integer quantity){
        if (id < 0 || quantity < 0)
            throw new InvalidInputException("Invalid id: " + id + " or quantity " + quantity + " provided.");

        InventoryItem updatedInventoryItem = this.inventoryService.addStock(id, quantity);
        return ResponseEntity.ok(updatedInventoryItem);
    }
}
