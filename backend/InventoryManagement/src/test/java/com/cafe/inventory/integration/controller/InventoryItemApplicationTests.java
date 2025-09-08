package com.cafe.inventory.integration.controller;

import com.cafe.inventory.entity.InventoryItem;
import com.cafe.inventory.service.PaginatedResponse;
import com.netflix.discovery.EurekaClient;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(InventoryItemApplicationTests.MockEurekaConfig.class)
class InventoryItemApplicationTests {
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
		return "http://localhost:" + port + "/api/inventory" + path;
	}

	@Test
	void shouldCreateAndFetchInventoryItem() {
		InventoryItem item = new InventoryItem();
		item.setMenuItemId(1);
		item.setStockLevel(10);
		item.setAvailable(true);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<InventoryItem> request = new HttpEntity<>(item, headers);

		// Create
		ResponseEntity<InventoryItem> postResponse =
				restTemplate.postForEntity(url(""), request, InventoryItem.class);
		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		InventoryItem created = postResponse.getBody();
		assertThat(created).isNotNull();
		assertThat(created.getMenuItemId()).isEqualTo(1);

		// Fetch by ID
		ResponseEntity<InventoryItem> getResponse =
				restTemplate.getForEntity(url("/" + created.getId()), InventoryItem.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getStockLevel()).isEqualTo(10);
	}

	@Test
	void shouldReturnPaginatedInventoryItems() {
		// Add a couple of items
		InventoryItem item1 = new InventoryItem(null, 1, 5, true);
		InventoryItem item2 = new InventoryItem(null, 2, 3, true);
		restTemplate.postForEntity(url(""), item1, InventoryItem.class);
		restTemplate.postForEntity(url(""), item2, InventoryItem.class);

		ResponseEntity<PaginatedResponse<InventoryItem>> response =
				restTemplate.exchange(
						url("?page=0&size=10&sortBy=menuItemId&direction=asc"),
						HttpMethod.GET,
						null,
						new org.springframework.core.ParameterizedTypeReference<PaginatedResponse<InventoryItem>>() {}
				);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		PaginatedResponse<InventoryItem> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getData()).isNotEmpty();
		assertThat(body.getData().size()).isGreaterThanOrEqualTo(2);
	}

	@Test
	void shouldUpdateInventoryItem() {
		InventoryItem item = new InventoryItem(null, 1, 5, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item, InventoryItem.class).getBody();

		created.setStockLevel(20);
		HttpEntity<InventoryItem> request = new HttpEntity<>(created);
		ResponseEntity<InventoryItem> response = restTemplate.exchange(
				url("/" + created.getId()),
				HttpMethod.PUT,
				request,
				InventoryItem.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getStockLevel()).isEqualTo(20);
	}

	@Test
	void shouldDeleteInventoryItem() {
		InventoryItem item = new InventoryItem(null, 1, 5, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item, InventoryItem.class).getBody();

		ResponseEntity<Void> response = restTemplate.exchange(
				url("/" + created.getId()), HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<InventoryItem> fetchResponse =
				restTemplate.getForEntity(url("/" + created.getId()), InventoryItem.class);
		assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldAddStock() {
		InventoryItem item = new InventoryItem(null, 1, 5, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item, InventoryItem.class).getBody();

		ResponseEntity<InventoryItem> response = restTemplate.exchange(
				url("/" + created.getId() + "/add"),
				HttpMethod.PUT,
				new HttpEntity<>(5),
				InventoryItem.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getStockLevel()).isEqualTo(10);
		assertTrue(response.getBody().isAvailable());
	}

	@Test
	void shouldReduceStock() {
		InventoryItem item1 = new InventoryItem(1, 3, 10, true);
		InventoryItem item2 = new InventoryItem(2, 4, 5, true);
		restTemplate.postForEntity(url(""), item1, InventoryItem.class);
		restTemplate.postForEntity(url(""), item2, InventoryItem.class);

		ResponseEntity<List<InventoryItem>> response = restTemplate.exchange(
				url("/reduce?menuItemIds=3,4&quantitiesOfMenuItems=3,5"),
				HttpMethod.PUT,
				null,
				new org.springframework.core.ParameterizedTypeReference<List<InventoryItem>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().get(0).getStockLevel()).isEqualTo(7);
		assertTrue(response.getBody().get(0).isAvailable());
		assertThat(response.getBody().get(1).getStockLevel()).isEqualTo(0);
		assertFalse(response.getBody().get(1).isAvailable());
	}

	@Test
	void shouldReturnNotFoundStockToReduce() {
		InventoryItem item1 = new InventoryItem(1, 3, 10, true);
		restTemplate.postForEntity(url(""), item1, InventoryItem.class);

		ResponseEntity<String> response = restTemplate.exchange(
				url("/reduce?menuItemIds=3&quantitiesOfMenuItems=30"),
				HttpMethod.PUT,
				null,
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldCheckAvailability() {
		InventoryItem item1 = new InventoryItem(null, 1, 5, true);
		InventoryItem item2 = new InventoryItem(null, 2, 2, true);
		restTemplate.postForEntity(url(""), item1, InventoryItem.class);
		restTemplate.postForEntity(url(""), item2, InventoryItem.class);

		ResponseEntity<List<Boolean>> response = restTemplate.exchange(
				url("/availability?menuItemIds=1,2&quantitiesOfMenuItems=3,20"),
				HttpMethod.GET,
				null,
				new org.springframework.core.ParameterizedTypeReference<List<Boolean>>() {}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsExactly(true, false);
	}
}
