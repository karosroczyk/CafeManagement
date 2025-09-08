package com.cafe.menumanagement.integration.controller;

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
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(CategoryManagementApplicationTests.MockEurekaConfig.class)
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
        Category coffeeCategory = new Category();
        coffeeCategory.setName("Coffee");
        coffeeCategory.setDescription("Hot beverages");
        ResponseEntity<Category> coffeeResponse =
                restTemplate.postForEntity(url(""), coffeeCategory, Category.class);
        Category savedCoffeeCategory = coffeeResponse.getBody();
        assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedCoffeeCategory).isNotNull();
        assertThat(savedCoffeeCategory.getName()).isEqualTo("Coffee");

        Category pastryCategory = new Category();
        pastryCategory.setName("Pastry");
        pastryCategory.setDescription("Something to eat");
        ResponseEntity<Category> pastryResponse =
                restTemplate.postForEntity(url(""), pastryCategory, Category.class);
        Category savedPastryCategory = pastryResponse.getBody();
        assertThat(pastryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedPastryCategory).isNotNull();
        assertThat(savedPastryCategory.getName()).isEqualTo("Pastry");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(url(""), new HttpEntity<>(coffeeCategory, headers), Category.class);
        restTemplate.postForEntity(url(""), new HttpEntity<>(pastryCategory, headers), Category.class);

        ResponseEntity<PaginatedResponse<Category>> response = restTemplate.exchange(
                url("?page=0&size=5&sortBy=name&direction=asc"),
                HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<Category>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaginatedResponse<Category> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getData()).isNotEmpty();
        assertThat(body.getData().size()).isEqualTo(2);
        assertThat(body.getData())
                .extracting(Category::getName)
                .contains("Coffee", "Pastry");
    }

    @Test
    void shouldUpdateCategory() {
        Category newItem = new Category();
        newItem.setName("Iced Coffee");
        newItem.setDescription("Cold beverages");

        Category created = restTemplate.postForEntity(url(""), newItem, Category.class).getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Cold beverages");

        created.setDescription("Cold coffee beverages");

        HttpEntity<Category> request = new HttpEntity<>(created);
        ResponseEntity<Category> response = restTemplate.exchange(
                url("/" + created.getId()),
                HttpMethod.PUT,
                request,
                Category.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDescription()).contains("coffee");
    }

    @Test
    void shouldDeleteCategory() {
        Category newItem = new Category();
        newItem.setName("Mocha Coffee");
        newItem.setDescription("Hot mocha beverages");

        Category created = restTemplate.postForEntity(url(""), newItem, Category.class).getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Hot mocha beverages");

        ResponseEntity<Category> getResponse =
                restTemplate.getForEntity(url("/" + created.getId()), Category.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Mocha Coffee");

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/" + created.getId()), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Category> fetchResponse =
                restTemplate.getForEntity(url("/" + created.getId()), Category.class);
        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
