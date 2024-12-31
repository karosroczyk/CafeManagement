package com.cafe.inventory.dao;

import com.cafe.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryDAOJPA extends JpaRepository<InventoryItem, Integer> {
    Optional<InventoryItem> findByMenuItemId(Integer menuItemId);
}
