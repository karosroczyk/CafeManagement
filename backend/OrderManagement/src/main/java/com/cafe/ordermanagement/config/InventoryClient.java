package com.cafe.ordermanagement.config;

import com.cafe.ordermanagement.dto.MenuItemDTO;
import com.cafe.ordermanagement.exception.ResponseStatusException;
import com.cafe.ordermanagement.service.PaginatedResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "checkAvailabilityFailure"
    )
    public List<Boolean> checkAvailability(List<Integer> menuItemIds, List<Integer> quantitiesOfMenuItems) {
        return webClientBuilder.build()
                .get()
                .uri(UriComponentsBuilder
                        .fromHttpUrl("http://inventory/api/inventory/availability")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .queryParam("quantitiesOfMenuItems", quantitiesOfMenuItems.toArray())
                        .toUriString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Boolean>>() {})
                .block();
    }
    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "decreaseStockFailure"
    )
    public void decreaseStock(List<Integer> menuItemIds, List<Integer> quantitiesOfMenuItems){
        webClientBuilder.build()
                .patch()
                .uri(UriComponentsBuilder
                        .fromHttpUrl("http://inventory/api/inventory/stock")
                        .queryParam("menuItemIds", menuItemIds.toArray())
                        .queryParam("quantitiesOfMenuItems", quantitiesOfMenuItems.toArray())
                        .toUriString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
    private List<Boolean> checkAvailabilityFailure(
            List<Integer> menuItemIds,
            List<Integer> quantitiesOfMenuItems,
            Throwable exception) {

        logFallback("checkAvailability", exception);
        throw serviceUnavailable(exception);
    }
    private void decreaseStockFailure(
            List<Integer> menuItemIds,
            List<Integer> quantitiesOfMenuItems,
            Throwable exception) {

        logFallback("decreaseStock", exception);
        throw serviceUnavailable(exception);
    }
    private void logFallback(String method, Throwable exception) {
        System.err.println("Circuit breaker fallback called: " + method);
        System.err.println("Reason: " + exception.getMessage());
    }

    private ResponseStatusException serviceUnavailable(Throwable exception) {
        return new ResponseStatusException("Inventory Service is temporarily unavailable.");
    }
}
