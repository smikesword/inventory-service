package com.twise.inventory_service;

import com.twise.inventory_service.models.Item;
import com.twise.inventory_service.models.Tag;
import com.twise.inventory_service.repositories.JdbcInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@DataJdbcTest
@Import(JdbcInventoryRepository.class)
public class JdbcInventoryRepositoryTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));

	@Autowired
	private JdbcInventoryRepository jdbcInventoryRepository;

	@Test
	void ableToConnectToDB(){
		assertThat(postgresContainer.isCreated()).isTrue();
		assertThat(postgresContainer.isRunning()).isTrue();
	}


	@Test
	void findAllShouldReturnAllItems(){
		List<Item> items = jdbcInventoryRepository.findAll();
		assertThat(jdbcInventoryRepository.findAll().size()).isEqualTo(2);
		assertThat(items.get(1).getName()).isEqualTo("Canned Black Beans");
		assertThat(items.get(0).getName()).isEqualTo("Organic Whole Milk");

		// Assert: Verify the LEFT JOIN correctly populated the tags for the first item
		Item milk = items.get(0);
		assertThat(milk.getQuantity()).isEqualTo(5);
		assertThat(milk.getQuantityUnit()).isEqualTo("pc");
		assertThat(milk.getTags()).hasSize(1);
		assertThat(milk.getTags().get(0).key()).isEqualTo("food_type");
		assertThat(milk.getTags().get(0).value()).isEqualTo("dairy");

		// Assert: Verify the second item's tags
		Item beans = items.get(1);
		assertThat(beans.getTags()).hasSize(1);
		assertThat(beans.getTags().get(0).key()).isEqualTo("location");
		assertThat(beans.getTags().get(0).value()).isEqualTo("pantry");
	}

	@Test
	void findByIdShouldReturnItemWithTags() {
		// Arrange: Get a known ID from the init.sql data
		Long existingId = jdbcInventoryRepository.findAll().get(0).getId();

		// Act
		Optional<Item> result = jdbcInventoryRepository.findById(existingId);

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(existingId);
		assertThat(result.get().getTags()).isNotNull().hasSize(1);
	}

	@Test
	void findByIdShouldReturnEmptyWhenItemDoesNotExist() {
		// Act
		Optional<Item> result = jdbcInventoryRepository.findById(9999L);

		// Assert: Proves your Optional.empty() fix is working correctly
		assertThat(result).isEmpty();
	}

	@Test
	void deleteByIdShouldRemoveItem() {
		// Arrange
		Long existingId = jdbcInventoryRepository.findAll().get(0).getId();
		assertThat(jdbcInventoryRepository.findAll()).hasSize(2);

		// Act
		String result = jdbcInventoryRepository.deleteById(existingId);

		// Assert
		assertThat(result).isEqualTo("delete item with id " + existingId);
		assertThat(jdbcInventoryRepository.findAll()).hasSize(1);
		assertThat(jdbcInventoryRepository.findById(existingId)).isEmpty();
	}

	@Test
	void addShouldInsertNewItemAndNewTags() {
		// Arrange
		Item newItem = new Item(null, "Fresh Apples", 3.50, null, null, 12, "kg");
		Tag newTag = new Tag(null, null, "origin", "local");

		// Act
		Item savedItem = jdbcInventoryRepository.add(newItem, List.of(newTag));

		// Assert: Verify the returned object has the generated ID and tags
		assertThat(savedItem.getId()).isNotNull();
		assertThat(savedItem.getTags()).hasSize(1);

		// Assert: Verify it was actually persisted to the database
		List<Item> allItems = jdbcInventoryRepository.findAll();
		assertThat(allItems).hasSize(3); // 2 from init.sql + 1 new

		Item fetchedItem = allItems.stream()
				.filter(i -> "Fresh Apples".equals(i.getName()))
				.findFirst()
				.orElseThrow();
		assertThat(fetchedItem.getTags().get(0).key()).isEqualTo("origin");
	}

	@Test
	void addShouldReuseExistingTagNotCreateOne(){
		Item newItem = new Item(null, "queso", 5.00, null, null, 12, "kg");
		Tag newTag = new Tag(null, null, "food_type", "dairy");

		int numOfExistingTags = jdbcInventoryRepository.getTags().size();

		jdbcInventoryRepository.add(newItem, List.of(newTag));

		assertThat(jdbcInventoryRepository.getTags().size()).isEqualTo(numOfExistingTags);
		assertThat(jdbcInventoryRepository.findAll().size()).isEqualTo(3);
	}

	@Test
	void deleteItemFromTagShouldMakeItemWithNoTag(){
		Optional<Item> item = jdbcInventoryRepository.findById(1L);
		Long tagId = item.get().getTags().get(0).id();

		jdbcInventoryRepository.removeTagFromItem(1L, tagId);

		assertThat(jdbcInventoryRepository.findById(1L).get().getTags().size()).isOne();
	}

	@Test
	void getTagsShouldReturnAllTags(){
		assertThat(jdbcInventoryRepository.getTags()).hasSize(2);
		assertThat(jdbcInventoryRepository.getTags().getFirst().key()).isEqualTo("food_type");
		assertThat(jdbcInventoryRepository.getTags().getLast().key()).isEqualTo("location");
		assertThat(jdbcInventoryRepository.getTags().getFirst().value()).isEqualTo("dairy");
		assertThat(jdbcInventoryRepository.getTags().getLast().value()).isEqualTo("pantry");
	}
}
