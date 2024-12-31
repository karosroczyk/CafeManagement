package com.cafe.menumanagement.dao;

import com.cafe.menumanagement.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuItemDAOJPA extends JpaRepository<MenuItem, Integer> {
    @Query("SELECT m FROM MenuItem m WHERE " +
            "(LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(m.price >= :minPrice OR :minPrice IS NULL) AND " +
            "(m.price <= :maxPrice OR :maxPrice IS NULL)")
    Page<MenuItem> findByCriteria(@Param("name") String name,
                                  @Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice,
                                  Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.categoryId IN (SELECT c.id FROM Category c WHERE c.name = :categoryName)")
    Page<MenuItem> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);
}
