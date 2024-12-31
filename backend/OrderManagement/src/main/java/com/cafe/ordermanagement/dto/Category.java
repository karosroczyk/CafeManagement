package com.cafe.ordermanagement.entity;

import com.cafe.ordermanagement.dto.MenuItem;
import java.util.List;

public class Category {
    private Integer id;
    private String name;
    private String description;
    private List<MenuItem> menuItems;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Category{id=%d, name='%s', description='%s'}", id, name, description);
    }
}
