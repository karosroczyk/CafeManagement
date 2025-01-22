package com.cafe.ordermanagement.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class OrderMenuItemIdKey implements Serializable {
    @Column(name = "order_id")
    private Integer orderId;
    @Column(name = "menuItem_id")
    private Integer menuItemId;
    @Column(name = "quantity")
    private Integer quantity;
    public OrderMenuItemIdKey() {}

    public OrderMenuItemIdKey(Integer orderId, Integer menuItemId, Integer quantity) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "OrderMenuItemIdKey{" +
                "orderId=" + orderId +
                ", menuItemId=" + menuItemId +
                ", quantity=" + quantity +
                '}';
    }
}
