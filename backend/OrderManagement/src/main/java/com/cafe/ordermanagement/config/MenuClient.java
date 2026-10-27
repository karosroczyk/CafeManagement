package com.cafe.ordermanagement.config;

import com.cafe.ordermanagement.dto.CategoryDTO;
import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.exception.ClientErrorException;
import com.cafe.ordermanagement.exception.ResourceNotFoundException;
import com.cafe.ordermanagement.exception.ResponseStatusException;
import com.cafe.ordermanagement.exception.ServerErrorException;
import com.cafe.ordermanagement.service.PaginatedResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuClient {
    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(
            name = "menuService",
            fallbackMethod = "menuItemsUnavailable"
    )
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

    @CircuitBreaker(
            name = "menuService",
            fallbackMethod = "menuItemCategoriesUnavailable"
    )
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

    @CircuitBreaker(
            name = "menuService",
            fallbackMethod = "menuItemsByCategoryUnavailable"
    )
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
            fallbackMethod = "pricesUnavailable"
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

    private PaginatedResponse<MenuItemDTO> menuItemsUnavailable(
            int page,
            int size,
            String[] sortBy,
            String[] direction,
            Throwable exception) {

        logFallback("getAllMenuItems", exception);
        throw serviceUnavailable(exception);
    }
    private PaginatedResponse<CategoryDTO> menuItemCategoriesUnavailable(
            int page,
            int size,
            String[] sortBy,
            String[] direction,
            Throwable exception) {

        logFallback("getAllMenuItemCategories", exception);
        throw serviceUnavailable(exception);
    }
    private PaginatedResponse<MenuItemDTO> menuItemsByCategoryUnavailable(
          int page,
          int size,
          String[] sortBy,
          String[] direction,
          String categoryName,
          Throwable exception) {

        logFallback("getAllMenuItemsByCategory", exception);
        throw serviceUnavailable(exception);
    }
    private Map<Integer, Double> pricesUnavailable(
            List<Integer> menuItemIds, Throwable exception) {

        logFallback("getPrices", exception);
        throw serviceUnavailable(exception);
    }

    private void logFallback(String method, Throwable exception) {
        System.err.println("Circuit breaker fallback called: " + method);
        System.err.println("Reason: " + exception.getMessage());
    }

    private ResponseStatusException serviceUnavailable(Throwable exception) {
        return new ResponseStatusException("Menu Service is temporarily unavailable.");
    }
}
