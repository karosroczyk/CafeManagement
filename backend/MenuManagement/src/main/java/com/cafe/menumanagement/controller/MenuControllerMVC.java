package com.cafe.menumanagement.controller;

import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;
import com.cafe.menumanagement.exception.InvalidInputException;
import com.cafe.menumanagement.service.MenuItemService;
import com.cafe.menumanagement.service.PaginatedResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/menus")
public class MenuControllerMVC {

    private final MenuItemService menuItemService;
    private final CategoryController categoryService;
    private String inventoryServiceUrl;

    public MenuControllerMVC(MenuItemService menuItemService, CategoryController categoryService) {
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getAllMenuItemsByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String[] sortBy,
            @RequestParam(defaultValue = "asc") String[] direction,
            Model model) {
        if (page < 0 || size <= 0 || sortBy.length != direction.length)
            throw new InvalidInputException("Invalid page: " + page + " or size: " + size + " provided.");

        Map<String, List<MenuItem>> categorizedMenuItems = new LinkedHashMap<>();
        List<Category> categories =
                categoryService.getAllCategories(page, size, sortBy, direction).getBody().getData();

        categories.stream().forEach(
                category -> {categorizedMenuItems.put(
                        category.getName(), this.menuItemService.getMenuItemsByCategoryName(category.getName(),page, size, sortBy, direction).getData());});

        model.addAttribute("categorizedMenuItems", categorizedMenuItems);
        return "menuitems";
    }
}
