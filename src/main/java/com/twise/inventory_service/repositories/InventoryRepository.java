package com.twise.inventory_service.repositories;

import com.twise.inventory_service.models.Item;
import com.twise.inventory_service.models.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public interface InventoryRepository {
    Iterable<Item> findAll();
    Optional<Item> findById(Long id);
//    Iterable<Item> findItemsByTag(Tag tag);
    Item add(Item item, List<Tag> tags);
//    String editById(Long id);
    String deleteById(Long id);
    Iterable<Tag> getTags();
    String removeTagFromItem(Long itemId, Long tagId);
}
