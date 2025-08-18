package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.CategoryService;
import com.cafe.menumanagement.service.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories_Success() {
        List<Category> categoryList = Arrays.asList(
                new Category("Beverages", "Hot and cold drinks"),
                new Category("Snacks", "Various snacks")
        );
        PaginatedResponse<Category> paginatedResponse = new PaginatedResponse<>(categoryList, 0, 1, 2, 2);
        when(categoryService.getAllCategories(anyInt(), anyInt(), any(String[].class), any(String[].class)))
                .thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<Category>> response = categoryController.getAllCategories(0, 2, new String[]{"name"}, new String[]{"asc"});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(categoryService, times(1)).getAllCategories(anyInt(), anyInt(), any(String[].class), any(String[].class));
    }

    @Test
    void testGetAllCategories_InvalidInput() {
        assertThrows(InvalidInputException.class, () -> {
            categoryController.getAllCategories(-1, 0, new String[]{"name"}, new String[]{"asc"});
        });
    }

    @Test
    void testGetCategoryById_Success() {
        Category category = new Category("Beverages", "Hot and cold drinks");
        when(categoryService.getCategoryById(anyInt())).thenReturn(category);

        ResponseEntity<Category> response = categoryController.getCategoryById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Beverages", response.getBody().getName());
        verify(categoryService, times(1)).getCategoryById(anyInt());
    }

    @Test
    void testGetCategoryById_InvalidInput() {
        assertThrows(InvalidInputException.class, () -> {
            categoryController.getCategoryById(-1);
        });
    }

    @Test
    void testCreateCategory_Success() {
        Category category = new Category("Beverages", "Hot and cold drinks");
        category.setId(1);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.createCategory(category, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void testCreateCategory_InvalidInput() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new org.springframework.validation.FieldError("category", "name", "Name is required"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            categoryController.createCategory(new Category(), bindingResult);
        });
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    void testUpdateCategory_Success() {
        Category category = new Category("Beverages", "Hot and cold drinks");
        category.setId(1);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(categoryService.updateCategory(anyInt(), any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.updateCategory(1, category, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(categoryService, times(1)).updateCategory(anyInt(), any(Category.class));
    }

    @Test
    void testUpdateCategory_InvalidInput() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new org.springframework.validation.FieldError("category", "name", "Name is required"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            categoryController.updateCategory(1, new Category(), bindingResult);
        });
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    void testDeleteCategory_Success() {
        ResponseEntity<Void> response = categoryController.deleteCategory(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(anyInt());
    }

    @Test
    void testDeleteCategory_InvalidInput() {
        assertThrows(InvalidInputException.class, () -> {
            categoryController.deleteCategory(-1);
        });
    }
}
