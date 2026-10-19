package com.cafe.ordermanagement.config;

import com.cafe.ordermanagement.dto.CategoryDTO;
import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.exception.ResourceNotFoundException;
import com.cafe.ordermanagement.service.PaginatedResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuClient {
    private final WebClient.Builder webClientBuilder;

    public PaginatedResponse<MenuItemDTO> getAllMenuItems(int page, int size, String[] sortBy, String[] direction) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("menu")
                        .path("/api/menuitems")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", (Object[]) sortBy)
                        .queryParam("direction", (Object[]) direction)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<
                        PaginatedResponse<MenuItemDTO>>() {})
                .block();
    }

    public PaginatedResponse<CategoryDTO> getAllMenuItemCategories(
            int page, int size, String[] sortBy, String[] direction) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("menu")
                        .path("/api/categories")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", (Object[]) sortBy)
                        .queryParam("direction", (Object[]) direction)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<
                        PaginatedResponse<CategoryDTO>>() {})
                .block();
    }

    public PaginatedResponse<MenuItemDTO> getAllMenuItemsByCategory(
            int page, int size, String[] sortBy, String[] direction, String categoryName){
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("menu")
                        .path("/api/menuitems/filter/category-name")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", (Object[]) sortBy)
                        .queryParam("direction", (Object[]) direction)
                        .queryParam("categoryName", categoryName)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<
                        PaginatedResponse<MenuItemDTO>>() {})
                .block();
    }
    @CircuitBreaker(
            name = "menuService",
            fallbackMethod = "menuUnavailable"
    )
    public Map<Integer, Double> getPrices(List<Integer> menuItemIds) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("menu")
                        .path("/api/menuitems/prices")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<
                        Map<Integer, Double>>() {})
                .block();
    }

    //TODO: add appropriate kind of exception
    private Map<Integer, Double> menuUnavailable(
            List<Integer> menuItemIds,
            Throwable exception) {
        throw new ResourceNotFoundException(
                "Menu Service is temporarily unavailable."
        );
    }
}
