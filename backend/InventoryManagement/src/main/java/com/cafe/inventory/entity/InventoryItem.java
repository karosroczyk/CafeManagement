package com.cafe.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "inventory")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "menu_item_id")
    @NotNull(message="MenuItem Id is required")
    @Min(value = 0, message = "MenuItem Id can't be less than 0")
    private Integer menuItemId;
    @Column(name = "stock_level")
    @NotNull(message="Stock Level is required")
    @Min(value = 0, message = "Stock Level can't be less than 0")
    private Integer stockLevel;
    @Column(name = "is_available")
    private Boolean isAvailable;
    public InventoryItem(){}
    public InventoryItem(Integer id, Integer menuItemId, Integer stockLevel, Boolean isAvailable){
        this.id = id;
        this.menuItemId = menuItemId;
        this.stockLevel = stockLevel;
        this.isAvailable = isAvailable;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public Integer getStockLevel() { return stockLevel; }
    public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }

    public Boolean isAvailable() { return isAvailable; }
    public void setAvailable(Boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", menuItemId=" + menuItemId +
                ", stockLevel=" + stockLevel +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
