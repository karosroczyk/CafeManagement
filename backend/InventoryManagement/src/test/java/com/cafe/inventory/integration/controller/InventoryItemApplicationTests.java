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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(InventoryItemApplicationTests.MockEurekaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
		InventoryItem item = new InventoryItem(null, 1, 10, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item, InventoryItem.class).getBody();
		assertNotNull(created);
		int addedQuantity = 5;

		String requestUrl = url("/stock?menuItemIds=" + created.getId() + "&quantitiesOfMenuItems=" + addedQuantity);

		ResponseEntity<InventoryItem[]> response = restTemplate.exchange(
				requestUrl,
				HttpMethod.PATCH,
				HttpEntity.EMPTY,
				InventoryItem[].class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().length).isEqualTo(1);

		InventoryItem updated = response.getBody()[0];
		assertThat(updated.getStockLevel()).isEqualTo(15);
		assertTrue(updated.isAvailable());
	}

	@Test
	void shouldAReduceStock() {
		InventoryItem item = new InventoryItem(null, 1, 10, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item, InventoryItem.class).getBody();
		assertNotNull(created);
		int addedQuantity = -5;

		String requestUrl = url("/stock?menuItemIds=" + created.getId() + "&quantitiesOfMenuItems=" + addedQuantity);

		ResponseEntity<InventoryItem[]> response = restTemplate.exchange(
				requestUrl,
				HttpMethod.PATCH,
				HttpEntity.EMPTY,
				InventoryItem[].class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().length).isEqualTo(1);

		InventoryItem updated = response.getBody()[0];
		assertThat(updated.getStockLevel()).isEqualTo(5);
		assertTrue(updated.isAvailable());
	}

	@Test
	void shouldReturnNotFoundStockToReduce() {
		InventoryItem item1 = new InventoryItem(null, 3, 10, true);
		InventoryItem created = restTemplate.postForEntity(url(""), item1, InventoryItem.class).getBody();
		assertNotNull(created);

		int fakeId = 999;
		int quantity = -5;

		String requestUrl = url("/stock?menuItemIds=" + fakeId + "&quantitiesOfMenuItems=" + quantity);

		ResponseEntity<String> response = restTemplate.exchange(
				requestUrl,
				HttpMethod.PATCH,
				HttpEntity.EMPTY,
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
