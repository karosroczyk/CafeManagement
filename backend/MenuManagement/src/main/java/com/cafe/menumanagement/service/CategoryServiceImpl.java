package com.cafe.menumanagement.service;

import com.cafe.menumanagement.dao.CategoryDAOJPA;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.exception.DatabaseUniqueValidationException;
import com.cafe.menumanagement.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryDAOJPA categoryDAOJPA;
    public CategoryServiceImpl(CategoryDAOJPA categoryDAOJPA){
        this.categoryDAOJPA = categoryDAOJPA;
    }
    @Override
    @Cacheable(value = "categoryItems", key = "'all:' + #page + ':' + #size + ':' + #sortBy + ':' + #direction")
    public PaginatedResponse<Category> getAllCategories(int page, int size, String[] sortBy, String[] direction) {
        List<Sort.Order> orders = IntStream.range(0, sortBy.length)
                .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(direction[i]), sortBy[i]))
                .toList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Category> categoryPage = this.categoryDAOJPA.findAll(pageable);

        return new PaginatedResponse<>(
                categoryPage.getContent(),
                categoryPage.getNumber(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements(),
                categoryPage.getSize());
    }

    @Override
    public Category getCategoryById(Integer id) {
        return this.categoryDAOJPA.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + id + " not found."));
    }
    @Override
    @Transactional
    @CacheEvict(value = "menuItems", allEntries = true)
    public Category createCategory(Category category) {
        try {
            return this.categoryDAOJPA.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItems", allEntries = true)
    public Category updateCategory(Integer id, Category category) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        try {
            return this.categoryDAOJPA.save(existingCategory);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItems", allEntries = true)
    public void deleteCategory(Integer id) {
        getCategoryById(id);
        try {
            this.categoryDAOJPA.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseUniqueValidationException(e.getRootCause().getMessage());
        }
    }
}
