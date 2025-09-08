package com.cafe.menumanagement.unit.service;

import com.cafe.menumanagement.dao.CategoryDAOJPA;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.exception.ResourceNotFoundException;
import com.cafe.menumanagement.service.CategoryServiceImpl;
import com.cafe.menumanagement.service.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryDAOJPA categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategoriesWithSorting() {
        Category beverages = new Category("Beverages", "Drinks such as coffee, tea, juices");
        Category desserts = new Category("Desserts", "Cakes, pies, pastries");

        String[] sortingFields = {"name"};
        String[] directions = {"asc"};

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortingFields.length; i++) {
            Sort.Direction direction = directions[i].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, sortingFields[i]));
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<Category> categoryPage = new PageImpl<>(Arrays.asList(beverages, desserts));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        PaginatedResponse<Category> result = categoryService.getAllCategories(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals("Beverages", result.getData().get(0).getName());
        assertEquals("Desserts", result.getData().get(1).getName());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCategoryById_Correct() {
        Category beverages = new Category("Beverages", "Drinks such as coffee, tea, juices");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(beverages));
        Category result = categoryService.getCategoryById(1);

        assertEquals("Beverages", result.getName());
        assertEquals(beverages, result);
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    void testGetCategoryById_IdNotFound() {
        Integer wrongId = 100;

        when(categoryRepository.findById(wrongId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(wrongId)
        );

        assertEquals("Category with id: " + wrongId + " not found.", exception.getMessage());
        verify(categoryRepository, times(1)).findById(wrongId);
    }

    @Test
    void testCreateCategory() {
        Category beverages = new Category("Beverages", "Drinks such as coffee, tea, juices");

        when(categoryRepository.save(beverages)).thenReturn(beverages);
        Category result = categoryService.createCategory(beverages);

        assertEquals("Beverages", result.getName());
        assertEquals(beverages, result);
        verify(categoryRepository, times(1)).save(beverages);
    }

    @Test
    void testUpdateCategory_Correct() {
        Category beverages = new Category("Beverages", "Drinks such as coffee, tea, juices");
        Category updatedBeverages = new Category("Beverages", "Hot and cold beverages");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(beverages));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedBeverages);
        Category result = categoryService.updateCategory(1, updatedBeverages);

        assertEquals(updatedBeverages, result);
        assertEquals("Hot and cold beverages", result.getDescription());
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory_Correct() {
        Category beverages = new Category("Beverages", "Drinks such as coffee, tea, juices");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(beverages));
        categoryService.deleteCategory(1);

        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).deleteById(1);
    }
}
