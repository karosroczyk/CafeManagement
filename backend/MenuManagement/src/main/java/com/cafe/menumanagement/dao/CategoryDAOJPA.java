package com.cafe.menumanagement.dao;

import com.cafe.menumanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAOJPA extends JpaRepository<Category, Integer> {

}
