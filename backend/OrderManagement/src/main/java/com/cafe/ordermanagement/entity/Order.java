package com.cafe.ordermanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="status")
    @NotBlank(message = "Order item status is required")
    private String status;

    @Column(name = "total_price")
    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price can't be less than 0")
    @Max(value = 10000, message = "Total price can't be greater than 10 000")
    private Double total_price;

    @Column(name = "customer_id")
    private Integer customerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderMenuItemId> menuItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Order() {
        this.status = "PENDING";
        this.total_price = 0.0;
        this.menuItems = new ArrayList<>();
    }

    public Order(Integer customerId) {
        this.status = "PENDING";
        this.total_price = 0.0;
        this.customerId = customerId;
        this.menuItems = new ArrayList<>();
    }

    public Order(String status, Double total_price, Integer customerId) {
        this.status = status;
        this.total_price = total_price;
        this.customerId = customerId;
        this.menuItems = new ArrayList<>();
    }
    public void addMenuItem(OrderMenuItemId menuItem) {
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
        menuItems.add(menuItem);
        menuItem.setOrder(this);  // Maintain the bidirectional relationship
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderMenuItemId> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<OrderMenuItemId> menuItems) {
        this.menuItems = menuItems;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Order{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", total_price=" + total_price +
                ", customerId=" + customerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
