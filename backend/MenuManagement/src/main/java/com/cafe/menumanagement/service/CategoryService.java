package com.cafe.menumanagement.service;

import com.cafe.menumanagement.entity.Category;

public interface CategoryService {
    PaginatedResponse<Category> getAllCategories(int page, int size, String[] sortBy, String[] direction);
    Category getCategoryById(Integer id);
    Category createCategory(Category category);
    Category updateCategory(Integer id, Category category);
    void deleteCategory(Integer id);
}
