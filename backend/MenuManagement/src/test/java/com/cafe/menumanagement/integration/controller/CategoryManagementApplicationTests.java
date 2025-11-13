package com.cafe.menumanagement.integration.controller;

import com.cafe.menumanagement.dto.CategoryDTO;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.service.PaginatedResponse;
import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(CategoryManagementApplicationTests.MockEurekaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryManagementApplicationTests {
    @TestConfiguration
    static class MockEurekaConfig {
        @Bean
        public EurekaClient mockEurekaClient() {
            return Mockito.mock(EurekaClient.class);
        }
    }
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + "/api/categories" + path;
    }
    @Test
    void shouldReturnPaginatedCategories() {
        CategoryDTO coffeeCategory = new CategoryDTO(1, "Coffee", "Hot beverages");
        ResponseEntity<CategoryDTO> coffeeResponse =
                restTemplate.postForEntity(url(""), coffeeCategory, CategoryDTO.class);
        CategoryDTO savedCoffeeCategory = coffeeResponse.getBody();
        assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedCoffeeCategory).isNotNull();
        assertThat(savedCoffeeCategory.name()).isEqualTo("Coffee");

        CategoryDTO pastryCategory = new CategoryDTO(2, "Pastry", "Something to eat");
        ResponseEntity<CategoryDTO> pastryResponse =
                restTemplate.postForEntity(url(""), pastryCategory, CategoryDTO.class);
        CategoryDTO savedPastryCategory = pastryResponse.getBody();
        assertThat(pastryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedPastryCategory).isNotNull();
        assertThat(savedPastryCategory.name()).isEqualTo("Pastry");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(url(""), new HttpEntity<>(coffeeCategory, headers), CategoryDTO.class);
        restTemplate.postForEntity(url(""), new HttpEntity<>(pastryCategory, headers), CategoryDTO.class);

        ResponseEntity<PaginatedResponse<CategoryDTO>> response = restTemplate.exchange(
                url("?page=0&size=5&sortBy=name&direction=asc"),
                HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<CategoryDTO>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaginatedResponse<CategoryDTO> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getData()).isNotEmpty();
        assertThat(body.getData().size()).isEqualTo(2);
        assertThat(body.getData())
                .extracting(CategoryDTO::name)
                .contains("Coffee", "Pastry");
    }

    @Test
    void shouldUpdateCategory() {
        CategoryDTO newItem = new CategoryDTO(1, "Iced Coffee", "Cold beverages");

        CategoryDTO created = restTemplate.postForEntity(url(""), newItem, CategoryDTO.class).getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.description()).isEqualTo("Cold beverages");

        CategoryDTO newItemUpdated = new CategoryDTO(newItem.id(), newItem.name(), "Cold coffee beverages");

        HttpEntity<CategoryDTO> request = new HttpEntity<>(newItemUpdated);
        ResponseEntity<CategoryDTO> response = restTemplate.exchange(
                url("/" + newItemUpdated.id()),
                HttpMethod.PUT,
                request,
                CategoryDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().description()).contains("coffee");
    }

    @Test
    void shouldDeleteCategory() {
        CategoryDTO newItem = new CategoryDTO(1, "Mocha Coffee", "Hot mocha beverages");

        CategoryDTO created = restTemplate.postForEntity(url(""), newItem, CategoryDTO.class).getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.description()).isEqualTo("Hot mocha beverages");

        ResponseEntity<CategoryDTO> getResponse =
                restTemplate.getForEntity(url("/" + created.id()), CategoryDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name()).isEqualTo("Mocha Coffee");

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/" + created.id()), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<CategoryDTO> fetchResponse =
                restTemplate.getForEntity(url("/" + created.id()), CategoryDTO.class);
        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
