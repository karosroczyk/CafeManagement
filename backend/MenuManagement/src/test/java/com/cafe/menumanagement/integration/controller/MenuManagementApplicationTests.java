package com.cafe.menumanagement.integration.controller;

import com.cafe.menumanagement.dto.MenuItemDTO;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(MenuManagementApplicationTests.MockEurekaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
		Category drinks = new Category("Drinks", "Hot drinks");
		ResponseEntity<Category> categoryResponse =
				restTemplate.postForEntity("http://localhost:" + port + "/api/categories", drinks, Category.class);
		assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Category savedCategory = categoryResponse.getBody();
		assertThat(savedCategory).isNotNull();

		MenuItemDTO item1 = new MenuItemDTO(1, "Espresso", "Strong coffee shot", 2.5, savedCategory.getId());
		MenuItemDTO item2 = new MenuItemDTO(2, "Cappuccino", "Coffee with foam", 3.0, savedCategory.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		restTemplate.postForEntity(url(""), new HttpEntity<>(item1, headers), MenuItemDTO.class);
		restTemplate.postForEntity(url(""), new HttpEntity<>(item2, headers), MenuItemDTO.class);

		ResponseEntity<PaginatedResponse<MenuItemDTO>> response = restTemplate.exchange(
				url("?page=0&size=5&sortBy=name&direction=asc"),
				HttpMethod.GET,
				null,
				new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<MenuItemDTO>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		PaginatedResponse<MenuItemDTO> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getData()).isNotEmpty();
		assertThat(body.getData().size()).isEqualTo(2);
		assertThat(body.getData())
				.extracting(MenuItemDTO::name)
				.contains("Espresso", "Cappuccino");

		List<Integer> menuItemIds = List.of(body.getData().get(0).id());
		ResponseEntity<Map> priceResponse =
				restTemplate.getForEntity(url("/prices?menuItemIds=" + menuItemIds.get(0)), Map.class);

		assertThat(priceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(priceResponse.getBody().get("2")).isEqualTo(3.0);
	}

	@Test
	void shouldFilterMenuItemsByCategoryName() {
		Category coffeeCategory = new Category("Drinks", "Hot drinks");
		ResponseEntity<Category> coffeeResponse =
				restTemplate.postForEntity(urlCategory(""), coffeeCategory, Category.class);
		Category savedCoffeeCategory = coffeeResponse.getBody();
		assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedCoffeeCategory).isNotNull();
		assertThat(savedCoffeeCategory.getName()).isEqualTo("Drinks");

		Category pastryCategory = new Category("Pastry", "Something to eat");
		ResponseEntity<Category> pastryResponse =
				restTemplate.postForEntity(urlCategory(""), pastryCategory, Category.class);
		Category savedPastryCategory = pastryResponse.getBody();
		assertThat(pastryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedPastryCategory).isNotNull();
		assertThat(savedPastryCategory.getName()).isEqualTo("Pastry");

		MenuItemDTO item1 = new MenuItemDTO(1, "Espresso", "Strong coffee shot", 2.5, savedCoffeeCategory.getId());
		restTemplate.postForEntity(url(""), item1, MenuItemDTO.class);

		MenuItemDTO item2 = new MenuItemDTO(2, "Cappuccino", "Coffee with foam", 3.0, savedPastryCategory.getId());
		restTemplate.postForEntity(url(""), item2, MenuItemDTO.class);

		String filterUrl = url("?page=0&size=5&sortBy=name&direction=asc&categoryName=Drinks");
		ResponseEntity<PaginatedResponse<MenuItemDTO>> response = restTemplate.exchange(
				filterUrl,
				HttpMethod.GET,
				null,
				new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<MenuItemDTO>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData()).isNotNull();
		assertThat(response.getBody().getData().get(0).name()).isEqualTo("Espresso");
	}

	@Test
	void shouldCreateAndFetchMenuItem() {
		Category coffeeCategory = new Category("Coffee", "Hot beverages");
		ResponseEntity<Category> coffeeResponse =
				restTemplate.postForEntity(urlCategory(""), coffeeCategory, Category.class);
		Category savedCoffeeCategory = coffeeResponse.getBody();
		assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedCoffeeCategory).isNotNull();
		assertThat(savedCoffeeCategory.getName()).isEqualTo("Coffee");

		MenuItemDTO newItem = new MenuItemDTO(1, "Latte", "Coffee shot with milk", 2.5, savedCoffeeCategory.getId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MenuItemDTO> request = new HttpEntity<>(newItem, headers);
		ResponseEntity<MenuItemDTO> postResponse =
				restTemplate.postForEntity(url(""), request, MenuItemDTO.class);

		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		MenuItemDTO createdItem = postResponse.getBody();
		assertThat(createdItem).isNotNull();
		assertThat(createdItem.id()).isNotNull();
		assertThat(createdItem.name()).isEqualTo("Latte");


		ResponseEntity<MenuItemDTO> getResponse =
				restTemplate.getForEntity(url("/" + createdItem.id()), MenuItemDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().name()).isEqualTo("Latte");
	}

	@Test
	void shouldReturnNotFoundForInvalidId() {
		ResponseEntity<String> response =
				restTemplate.getForEntity(url("/99999"), String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldUpdateMenuItem() {
		Category coffeeCategory = new Category("Coffee","Hot beverages");
		ResponseEntity<Category> coffeeResponse =
				restTemplate.postForEntity(urlCategory(""), coffeeCategory, Category.class);
		Category savedCoffeeCategory = coffeeResponse.getBody();
		assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedCoffeeCategory).isNotNull();
		assertThat(savedCoffeeCategory.getName()).isEqualTo("Coffee");

		MenuItemDTO newItem = new MenuItemDTO(1, "Latte", "Strong coffee", 2.5, savedCoffeeCategory.getId());

		MenuItemDTO created = restTemplate.postForEntity(url(""), newItem, MenuItemDTO.class).getBody();
		assertThat(created).isNotNull();
		assertThat(created.id()).isNotNull();
		assertThat(created.description()).isEqualTo("Strong coffee");
		assertThat(created.price()).isEqualTo(2.5);

		MenuItemDTO updated = new MenuItemDTO(created.id(), "Latte", "Strong coffee shot", 2.5, created.categoryId());

		HttpEntity<MenuItemDTO> request = new HttpEntity<>(updated);
		ResponseEntity<MenuItemDTO> response = restTemplate.exchange(
				url("/" + updated.id()),
				HttpMethod.PUT,
				request,
				MenuItemDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().price()).isEqualTo(2.5);
		assertThat(response.getBody().description()).contains("shot");
	}

	@Test
	void shouldDeleteMenuItem() {
		Category coffeeCategory = new Category("Coffee","Hot beverages");
		ResponseEntity<Category> coffeeResponse =
				restTemplate.postForEntity(urlCategory(""), coffeeCategory, Category.class);
		Category savedCoffeeCategory = coffeeResponse.getBody();
		assertThat(coffeeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(savedCoffeeCategory).isNotNull();
		assertThat(savedCoffeeCategory.getName()).isEqualTo("Coffee");

		MenuItemDTO newItem = new MenuItemDTO(1, "Mocha", "Chocolate coffee", 3.0, savedCoffeeCategory.getId());

		MenuItemDTO created = restTemplate.postForEntity(url(""), newItem, MenuItemDTO.class).getBody();
		assertThat(created).isNotNull();
		assertThat(created.id()).isNotNull();
		assertThat(created.description()).isEqualTo("Chocolate coffee");
		assertThat(created.price()).isEqualTo(3.0);

		ResponseEntity<MenuItemDTO> getResponse =
				restTemplate.getForEntity(url("/" + created.id()), MenuItemDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().name()).isEqualTo("Mocha");

		ResponseEntity<Void> response = restTemplate.exchange(
				url("/" + created.id()), HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<MenuItemDTO> fetchResponse =
				restTemplate.getForEntity(url("/" + created.id()), MenuItemDTO.class);
		assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
