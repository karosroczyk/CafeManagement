package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.dto.MenuItemDTO;
import com.cafe.menumanagement.dto.MenuItemMapper;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.exception.ResourceNotFoundException;
import com.cafe.menumanagement.service.CategoryService;
import com.cafe.menumanagement.service.MenuItemService;
import com.cafe.menumanagement.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menuitems")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;

    public MenuItemController(MenuItemService menuItemService, CategoryService categoryService)
        { this.menuItemService = menuItemService;
          this.categoryService = categoryService;
        }

    @GetMapping
    public ResponseEntity<PaginatedResponse<MenuItemDTO>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction){
        if(page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<MenuItem> menuItems =
                this.menuItemService.getAllMenuItems(page, size, sortBy, direction);

        List<MenuItemDTO> dtoList = menuItems.getData()
                        .stream()
                        .map(menuItem -> MenuItemMapper.toDTO(menuItem))
                        .toList();

        PaginatedResponse<MenuItemDTO> response = new PaginatedResponse<>(
                dtoList,
                menuItems.getCurrentPage(),
                menuItems.getTotalPages(),
                menuItems.getTotalElements(),
                menuItems.getSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        MenuItem menuItem = this.menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(MenuItemMapper.toDTO(menuItem));
    }
    @GetMapping("/{id}/price")
    public ResponseEntity<Double> getMenuItemPriceById(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        MenuItem menuItem = this.menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem.getPrice());
    }

    @GetMapping("/filter/category-name")
    public ResponseEntity<PaginatedResponse<MenuItemDTO>> getMenuItemsByCategoryName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            @RequestParam String categoryName) {
        PaginatedResponse<MenuItem> menuItems = this.menuItemService.getMenuItemsByCategoryName(categoryName, page, size, sortBy, direction);

        List<MenuItemDTO> dtoList = menuItems.getData()
                .stream()
                .map(menuItem -> MenuItemMapper.toDTO(menuItem))
                .toList();

        PaginatedResponse<MenuItemDTO> response = new PaginatedResponse<>(
                dtoList,
                menuItems.getCurrentPage(),
                menuItems.getTotalPages(),
                menuItems.getTotalElements(),
                menuItems.getSize()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO, BindingResult result){
        if (result.hasErrors()) {
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());
        }
        Category category = categoryService.getCategoryById(menuItemDTO.categoryId());

        MenuItem createdMenuItem =
                this.menuItemService.createMenuItem(MenuItemMapper.toEntity(menuItemDTO, category));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MenuItemMapper.toDTO(createdMenuItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Integer id, @Valid @RequestBody MenuItemDTO menuItemDTO, BindingResult result){
        if (result.hasErrors())
            throw new InvalidInputException("Invalid MenuItemDTO: " + result.getFieldError().getDefaultMessage() + " provided.");

        if (id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        Category category = categoryService.getCategoryById(menuItemDTO.categoryId());

        MenuItem updatedMenuItem =
                this.menuItemService.updateMenuItem(id, MenuItemMapper.toEntity(menuItemDTO, category));
        return ResponseEntity
                .ok(MenuItemMapper.toDTO(updatedMenuItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MenuItem> deleteMenuItem(@PathVariable Integer id){
        if(id < 0)
            throw new InvalidInputException("Invalid id: " + id + " provided.");

        this.menuItemService.deleteMenuItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
