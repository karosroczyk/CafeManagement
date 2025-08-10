package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.MenuItemService;
import com.cafe.menumanagement.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menuitems")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) { this.menuItemService = menuItemService; }

    @GetMapping
    public ResponseEntity<PaginatedResponse<MenuItem>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction){
        if(page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<MenuItem> menuItems = this.menuItemService.getAllMenuItems(page, size, sortBy, direction);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        MenuItem menuItem = this.menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }
    @GetMapping("/{id}/price")
    public ResponseEntity<Double> getMenuItemPriceById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        MenuItem menuItem = this.menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem.getPrice());
    }

    @GetMapping("/filter/category-name")
    public ResponseEntity<PaginatedResponse<MenuItem>> getMenuItemsByCategoryName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            @RequestParam String categoryName) {
        PaginatedResponse<MenuItem> menuItems = this.menuItemService.getMenuItemsByCategoryName(categoryName, page, size, sortBy, direction);
        return ResponseEntity.ok(menuItems);
    }
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItem menuItem, BindingResult result){
        if (result.hasErrors()) {
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());
        }

        MenuItem createdMenuItem = this.menuItemService.createMenuItem(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Integer id, @Valid @RequestBody MenuItem menuItem, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException("Invalid MenuItem: " + result.getFieldError().getDefaultMessage() + " provided.");

        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        MenuItem updatedMenuItem = this.menuItemService.updateMenuItem(id, menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MenuItem> deleteMenuItem(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.menuItemService.deleteMenuItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
