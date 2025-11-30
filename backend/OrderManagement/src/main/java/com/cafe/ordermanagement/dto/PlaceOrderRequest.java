package com.cafe.ordermanagement.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequest(
    @NotNull Integer customerId,
    List<Integer> menuItemIds,
    List<Integer> quantitiesOfMenuItems){
}
