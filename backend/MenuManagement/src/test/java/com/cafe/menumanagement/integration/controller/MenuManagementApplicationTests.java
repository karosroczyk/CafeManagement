package com.cafe.menumanagement.integration.controller;

import com.cafe.menumanagement.entity.Category;
import com.cafe.menumanagement.entity.MenuItem;
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
@Import(MenuManagementApplicationTests.MockEurekaConfig.class)
class MenuManagementApplicationTests {
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
		return "http://localhost:" + port + "/api/menuitems" + path;
	}
	private String urlCategory(String path) {
		return "http://localhost:" + port + "/api/categories" + path;
	}

	@Test
	void shouldReturnPaginatedMenuItemsAndCheckGetMenuItemPriceById() {
		MenuItem item1 = new MenuItem();
		item1.setName("Espresso");
		item1.setDescription("Strong coffee shot");
		item1.setPrice(2.5);
		item1.setCategoryId(1);

		MenuItem item2 = new MenuItem();
		item2.setName("Cappuccino");
		item2.setDescription("Coffee with foam");
		item2.setPrice(3.0);
		item2.setCategoryId(1);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		restTemplate.postForEntity(url(""), new HttpEntity<>(item1, headers), MenuItem.class);
		restTemplate.postForEntity(url(""), new HttpEntity<>(item2, headers), MenuItem.class);

		ResponseEntity<PaginatedResponse<MenuItem>> response = restTemplate.exchange(
				url("?page=0&size=5&sortBy=name&direction=asc"),
				HttpMethod.GET,
				null,
				new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<MenuItem>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		PaginatedResponse<MenuItem> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getData()).isNotEmpty();
		assertThat(body.getData().size()).isEqualTo(2);
		assertThat(body.getData())
				.extracting(MenuItem::getName)
				.contains("Espresso", "Cappuccino");

		ResponseEntity<Double> priceResponse =
				restTemplate.getForEntity(url("/" + body.getData().get(0).getItem_id() + "/price"), Double.class);

		assertThat(priceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(priceResponse.getBody()).isEqualTo(3.0);
	}

	@Test
	void shouldFilterMenuItemsByCategoryName() {
		Category coffeeCategory = new Category();
		coffeeCategory.setName("Coffee");
		coffeeCategory.setDescription("Hot beverages");
		ResponseEntity<Category> coffeeResponse =
				restTemplate.postForEntity(urlCategory(""), coffeeCategory, Category.class);
		Category savedCoffeeCategory = coffeeResponse.getBody();
		assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedCoffeeCategory).isNotNull();
		assertThat(savedCoffeeCategory.getName()).isEqualTo("Coffee");

		Category pastryCategory = new Category();
		pastryCategory.setName("Pastry");
		pastryCategory.setDescription("Something to eat");
		ResponseEntity<Category> pastryResponse =
				restTemplate.postForEntity(urlCategory(""), pastryCategory, Category.class);
		Category savedPastryCategory = pastryResponse.getBody();
		assertThat(pastryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedPastryCategory).isNotNull();
		assertThat(savedPastryCategory.getName()).isEqualTo("Pastry");

		MenuItem item1 = new MenuItem();
		item1.setName("Latte");
		item1.setDescription("Milk coffee");
		item1.setPrice(3.5);
		item1.setCategoryId(savedCoffeeCategory.getId());
		restTemplate.postForEntity(url(""), item1, MenuItem.class);

		MenuItem item2 = new MenuItem();
		item2.setName("Muffin");
		item2.setDescription("Sweet treat");
		item2.setPrice(2.0);
		item2.setCategoryId(savedPastryCategory.getId());
		restTemplate.postForEntity(url(""), item2, MenuItem.class);

		String filterUrl = url("/filter/category-name?categoryName=Coffee&page=0&size=5&sortBy=name&direction=asc");
		ResponseEntity<PaginatedResponse<MenuItem>> response = restTemplate.exchange(
				filterUrl,
				HttpMethod.GET,
				null,
				new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<MenuItem>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData()).isNotNull();
		assertThat(response.getBody().getData().get(0).getName()).isEqualTo("Cappuccino");
	}

	@Test
	void shouldCreateAndFetchMenuItem() {
		MenuItem newItem = new MenuItem();
		newItem.setName("Latte");
		newItem.setDescription("Coffee with milk");
		newItem.setPrice(3.5);
		newItem.setCategoryId(1);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MenuItem> request = new HttpEntity<>(newItem, headers);
		ResponseEntity<MenuItem> postResponse =
				restTemplate.postForEntity(url(""), request, MenuItem.class);

		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		MenuItem createdItem = postResponse.getBody();
		assertThat(createdItem).isNotNull();
		assertThat(createdItem.getItem_id()).isNotNull();
		assertThat(createdItem.getName()).isEqualTo("Latte");


		ResponseEntity<MenuItem> getResponse =
				restTemplate.getForEntity(url("/" + createdItem.getItem_id()), MenuItem.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getName()).isEqualTo("Latte");
	}

	@Test
	void shouldReturnNotFoundForInvalidId() {
		ResponseEntity<String> response =
				restTemplate.getForEntity(url("/99999"), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldUpdateMenuItem() {
		MenuItem newItem = new MenuItem();
		newItem.setName("Espresso");
		newItem.setDescription("Strong coffee");
		newItem.setPrice(2.0);
		newItem.setCategoryId(1);

		MenuItem created = restTemplate.postForEntity(url(""), newItem, MenuItem.class).getBody();
		assertThat(created).isNotNull();
		assertThat(created.getItem_id()).isNotNull();
		assertThat(created.getDescription()).isEqualTo("Strong coffee");
		assertThat(created.getPrice()).isEqualTo(2.0);

		created.setPrice(2.5);
		created.setDescription("Strong coffee shot");

		HttpEntity<MenuItem> request = new HttpEntity<>(created);
		ResponseEntity<MenuItem> response = restTemplate.exchange(
				url("/" + created.getItem_id()),
				HttpMethod.PUT,
				request,
				MenuItem.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getPrice()).isEqualTo(2.5);
		assertThat(response.getBody().getDescription()).contains("shot");
	}

	@Test
	void shouldDeleteMenuItem() {
		MenuItem newItem = new MenuItem();
		newItem.setName("Mocha");
		newItem.setDescription("Chocolate coffee");
		newItem.setPrice(3.0);
		newItem.setCategoryId(1);

		MenuItem created = restTemplate.postForEntity(url(""), newItem, MenuItem.class).getBody();
		assertThat(created).isNotNull();
		assertThat(created.getItem_id()).isNotNull();
		assertThat(created.getDescription()).isEqualTo("Chocolate coffee");
		assertThat(created.getPrice()).isEqualTo(3.0);

		ResponseEntity<MenuItem> getResponse =
				restTemplate.getForEntity(url("/" + created.getItem_id()), MenuItem.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getName()).isEqualTo("Mocha");

		ResponseEntity<Void> response = restTemplate.exchange(
				url("/" + created.getItem_id()), HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<MenuItem> fetchResponse =
				restTemplate.getForEntity(url("/" + created.getItem_id()), MenuItem.class);
		assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
