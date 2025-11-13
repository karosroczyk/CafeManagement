package com.cafe.ordermanagement.dto;
public record MenuItemDTO(
        Integer id,
        String name,
        String description,
        Double price,
        Integer categoryId
){}

