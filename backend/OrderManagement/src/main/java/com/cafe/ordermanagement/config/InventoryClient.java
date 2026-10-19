package com.cafe.ordermanagement.config;

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
}
