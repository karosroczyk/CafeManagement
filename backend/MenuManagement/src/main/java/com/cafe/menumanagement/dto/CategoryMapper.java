package com.cafe.menumanagement.dto;

import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;

import java.util.List;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category entity) {
        if (entity == null) return null;
        return new CategoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static Category toEntity(CategoryDTO dto, List<MenuItem> menuItems) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.id());
        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setMenuItems(menuItems);
        return category;
    }
}
