package com.twise.inventory_service.services;

import com.twise.inventory_service.models.Item;
import com.twise.inventory_service.models.ItemDoesNotExistsException;
import com.twise.inventory_service.models.Tag;
import com.twise.inventory_service.repositories.JdbcInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final JdbcInventoryRepository jdbcInventoryRepository;

    public InventoryService(JdbcInventoryRepository jdbcInventoryRepository){
        this.jdbcInventoryRepository = jdbcInventoryRepository;
    }

    public Item getItemById(String id){
        return jdbcInventoryRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ItemDoesNotExistsException(Long.valueOf(id)));
    }

    public Iterable<Item> getAllItems(){
        return jdbcInventoryRepository.findAll();
    }

    public Item addItem(Item item, List<Tag> tags){
        return jdbcInventoryRepository.add(item, tags);
    }

    public String deleteItemById(String id){
        return jdbcInventoryRepository.deleteById(Long.valueOf(id));
    }

    public String deleteTagFromItem(String itemId, String tagId) {
        return jdbcInventoryRepository.removeTagFromItem(Long.valueOf(itemId), Long.valueOf(tagId));
    }

    public Iterable<Tag> getAllTags(){ return jdbcInventoryRepository.getTags(); }
}
