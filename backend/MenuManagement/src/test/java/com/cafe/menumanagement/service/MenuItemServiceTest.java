package com.cafe.menumanagement.service;

import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.dao.MenuItemDAOJPA;
import com.cafe.menumanagement.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;

class MenuItemServiceTest {

    @Mock
    private MenuItemDAOJPA menuItemRepository;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadMenuItems() {
        MenuItem espresso = new MenuItem("Espresso", "Strong black coffee", 2.50, 1, null);
        MenuItem cappuccino = new MenuItem("Cappuccino", "Espresso with steamed milk", 3.00, 1, null);
        String[] sortingFields = {"name", "price"};
        String[] directions = {"asc", "desc"};

        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortingFields.length; i++) {
            Sort.Direction direction = directions[i].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, sortingFields[i]));
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by(orders));
        Page<MenuItem> menuItemPage = new PageImpl<>(Arrays.asList(cappuccino, espresso));

        when(menuItemRepository.findAll(pageable)).thenReturn(menuItemPage);
        PaginatedResponse<MenuItem> result = menuItemService.getAllMenuItems(0, 2, sortingFields, directions);

        assertEquals(2, result.getSize());
        assertEquals("Cappuccino", result.getData().get(0).getName());
        assertEquals("Espresso", result.getData().get(1).getName());
        verify(menuItemRepository, times(1)).findAll(pageable);
    }

    @Test
    void testReadMenuItemById_Correct() {
        MenuItem cappuccino = new MenuItem("Cappuccino", "Espresso with steamed milk", 3.00, 1, null);

        when(menuItemRepository.findById(1)).thenReturn(Optional.of(cappuccino));
        MenuItem result = menuItemService.getMenuItemById(1);

        assertEquals("Cappuccino", result.getName());
        assertEquals(cappuccino, result);
        verify(menuItemRepository, times(1)).findById(1);
    }

    @Test
    void testReadCategoryById_IdNotFound() {
        Integer wrongId = 100;

        when(menuItemRepository.findById(wrongId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> menuItemService.getMenuItemById(wrongId)
        );

        assertEquals("MenuItem with id: " + wrongId + " not found.", exception.getMessage());
        verify(menuItemRepository, times(1)).findById(wrongId);
    }

    @Test
    void testCreateMenuItem() {
        MenuItem menuItem = new MenuItem("Latte", "Espresso with steamed milk", 4.00, 1, null);

        when(menuItemRepository.save(menuItem)).thenReturn(menuItem);
        MenuItem result = menuItemService.createMenuItem(menuItem);

        assertEquals("Latte", result.getName());
        assertEquals(menuItem, result);
        verify(menuItemRepository, times(1)).save(menuItem);
    }

    @Test
    void testUpdateMenuItem_Correct() {
        MenuItem cappuccino = new MenuItem("Cappuccino", "Espresso with steamed milk", 3.00, 1, null);
        MenuItem updatedCappuccino = new MenuItem("Cappuccino", "Espresso with steamed, hot milk", 3.00, 1, null);

        when(menuItemRepository.findById(1)).thenReturn(Optional.of(cappuccino));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedCappuccino);
        MenuItem result = menuItemService.updateMenuItem(1, updatedCappuccino);

        assertEquals(updatedCappuccino, result);
        assertEquals("Espresso with steamed, hot milk", result.getDescription());
        verify(menuItemRepository, times(1)).findById(1);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testDeleteMenuItem_Correct() {
        MenuItem cappuccino = new MenuItem("Cappuccino", "Espresso with steamed milk", 3.00, 1, null);

        when(menuItemRepository.findById(1)).thenReturn(Optional.of(cappuccino));
        menuItemService.deleteMenuItem(1);

        verify(menuItemRepository, times(1)).findById(1);
        verify(menuItemRepository, times(1)).deleteById(1);
    }
}
