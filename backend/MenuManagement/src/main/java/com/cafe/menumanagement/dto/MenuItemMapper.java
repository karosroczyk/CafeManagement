package com.cafe.menumanagement.dto;

import com.cafe.menumanagement.dto.MenuItemDTO;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;

public class MenuItemMapper {

    public static MenuItemDTO toDTO(MenuItem menuItem) {
        if (menuItem == null) return null;

        return new MenuItemDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory() != null ? menuItem.getCategory().getId() : null
        );
    }

    public static MenuItem toEntity(MenuItemDTO dto, Category category) {
        if (dto == null) return null;

        MenuItem menuItem = new MenuItem();
            menuItem.setId(dto.id());
            menuItem.setName(dto.name());
            menuItem.setDescription(dto.description());
            menuItem.setPrice(dto.price());
            menuItem.setCategory(category);

        return menuItem;
    }
}