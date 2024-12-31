package com.cafe.ordermanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "Order_MenuItem_Id")
public class OrderMenuItemId {
    @EmbeddedId
    private OrderMenuItemIdKey orderMenuItemIdKey;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    public OrderMenuItemId() {
    }

    public OrderMenuItemId(OrderMenuItemIdKey orderMenuItemIdKey) {
        this.orderMenuItemIdKey = orderMenuItemIdKey;
    }

    public OrderMenuItemIdKey getOrderMenuItemIdKey() {
        return orderMenuItemIdKey;
    }

    public void setOrderMenuItemIdKey(OrderMenuItemIdKey orderMenuItemIdKey) {
        this.orderMenuItemIdKey = orderMenuItemIdKey;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderMenuItemId{" +
                "orderMenuItemIdKey=" + orderMenuItemIdKey +
                ", order=" + order +
                '}';
    }
}
