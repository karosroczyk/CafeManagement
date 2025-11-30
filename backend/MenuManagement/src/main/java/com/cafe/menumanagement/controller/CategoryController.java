package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.dto.CategoryDTO;
import com.cafe.menumanagement.dto.CategoryMapper;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.CategoryService;
import com.cafe.menumanagement.service.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction){
        if(page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + ", size: " + size + " provided.");

        PaginatedResponse<Category> categories = categoryService.getAllCategories(page, size, sortBy, direction);

        List<CategoryDTO> dtoList = categories.getData()
                .stream()
                .map(category -> CategoryMapper.toDTO(category))
                .toList();

        PaginatedResponse<CategoryDTO> response = new PaginatedResponse<>(
                dtoList,
                categories.getCurrentPage(),
                categories.getTotalPages(),
                categories.getTotalElements(),
                categories.getSize()
        );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id){
        if (id < 0) throw new InvalidInputException("Invalid ID provided.");

        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(CategoryMapper.toDTO(category));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category, BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());
        }

        Category createdCategory = categoryService.createCategory(CategoryMapper.toEntity(category, null));

        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toDTO(createdCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryDTO category, BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidInputException(result.getFieldError().getDefaultMessage());
        }

        if (id < 0) throw new InvalidInputException("Invalid ID provided.");
        Category updatedCategory = categoryService.updateCategory(id, CategoryMapper.toEntity(category, null));
        return ResponseEntity.ok(CategoryMapper.toDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        if (id < 0) throw new InvalidInputException("Invalid ID provided.");
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
