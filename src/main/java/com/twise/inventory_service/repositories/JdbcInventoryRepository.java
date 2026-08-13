package com.twise.inventory_service.repositories;

import com.twise.inventory_service.models.Item;
import com.twise.inventory_service.models.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcInventoryRepository implements InventoryRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcInventoryRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcInventoryRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private Item mapRowToItem(ResultSet row, int rowNum) throws SQLException {
        //because expirationDate is nullable
        var expirationDateField = row.getDate("expiration_date") != null ? row.getDate("date_bought").toLocalDate() : null;
        return new Item(
                Long.valueOf(row.getString("id")),
                row.getString("name"),
                Double.parseDouble(row.getString("price")),
                expirationDateField,
                row.getDate("expiration_date").toLocalDate(),
                Integer.valueOf(row.getString("quantity")),
                row.getString("quantity_unit")
        );
    }

    private Map<Pair<String, String>,Long> getKeyValuePairs(){
        Map<Pair<String, String>,Long> tags = new HashMap<>();
        jdbcTemplate.query(
                "select * from tag",
                (ResultSet rs) ->{
                    var pair = new Pair(rs.getString("key_tag"), rs.getString("value"));
                    tags.put(pair, Long.valueOf(rs.getString("id")));
                });
        return tags;
    }

    private Tag mapRowToTag(ResultSet row, int rowNum) throws SQLException {
        return Tag.of(
                row.getString("key_tag"),
                row.getString("value")
        );
    }


    @Override
    public Item add(Item newItem, List<Tag> tags){
        Long newItemId = jdbcTemplate.queryForObject(
                "INSERT INTO Item (name, price, expiration_date, quantity, quantity_unit) " +
                        "VALUES (?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                newItem.getName(),
                newItem.getPrice(),
                newItem.getExpirationDate(),
                newItem.getQuantity(),
                newItem.getQuantityUnit()
        );

        // Set the generated ID back to the object so we can use it
        newItem.setId(newItemId);

        Map<Pair<String, String>, Long> existingTags = getKeyValuePairs();

        //for each tag check if already in tag table if not add to tag table and item_tags table
        for(Tag tag: tags){
//            System.out.printf("Key: %s, Value: %s", tag.key(), tag.value());
            var pair = new Pair(tag.key(), tag.value());
            Long newTagId = null;
            if(!existingTags.containsKey(pair)){
               newTagId = jdbcTemplate.queryForObject(
                        "INSERT INTO Tag (key_tag, value) VALUES (?, ?) RETURNING id",
                        Long.class,
                       tag.key(),
                       tag.value());
            }
            if (newTagId == null) newTagId = existingTags.get(pair);

            jdbcTemplate.update(
                    "INSERT INTO item_tags (item_id, tag_id) VALUES (?, ?)",
                    newItemId,
                    newTagId);

        }

        newItem.setTags(tags);

        return newItem;
    }

    @Override
    public String deleteById(Long id){
        jdbcTemplate.update(
                "DELETE FROM item WHERE id=?",
                id
        );
        return "delete item with id " + id;
    }

    @Override
    public List<Item> findAll() {
        // 1. A single query that joins Item, item_tags, and Tag
        String sql = "SELECT i.id AS item_id, i.name, i.price, i.date_bought, i.expiration_date, " +
                "i.quantity, i.quantity_unit, " +
                "t.id AS tag_id, t.key_tag, t.value " +
                "FROM item i " +
                "LEFT JOIN item_tags it ON i.id = it.item_id " +
                "LEFT JOIN tag t ON it.tag_id = t.id";

        // We use a Map to keep track of items by their ID so we can add tags to them
        Map<Long, Item> itemMap = new LinkedHashMap<>();

        // 2. Process the rows as they come back from the database
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            Long itemId = rs.getLong("item_id");

            // If we haven't created this Item object yet, create it and add it to the map
            if (!itemMap.containsKey(itemId)) {
                var expirationDateField = rs.getDate("expiration_date") != null ? rs.getDate("date_bought").toLocalDate() : null;
                Item item = new Item(
                        itemId,
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDate("date_bought").toLocalDate(),
                        expirationDateField,
                        Integer.valueOf(rs.getString("quantity")),
                        rs.getString("quantity_unit")
                );
                // Initialize an empty list for tags
                item.setTags(new ArrayList<>());
                itemMap.put(itemId, item);
            }

            // Because we used LEFT JOIN, tag_id might be null if the item has no tags
            Long tagId = rs.getObject("tag_id", Long.class);
            if (tagId != null) {
                Tag tag = new Tag(
                        tagId,
                        itemId, // Pass itemId here if your Tag constructor requires it
                        rs.getString("key_tag"),
                        rs.getString("value")
                );
                // Add the tag to the correct item
                itemMap.get(itemId).getTags().add(tag);
            }
        });

        // 3. Return the grouped items
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Optional<Item> findById(Long id){
        List<Item> resItem = jdbcTemplate.query(
                "select * from item where id=?",
                this::mapRowToItem,
                id
        );

        LOGGER.info("Item with id: {} doesn't exists", id);
        if(resItem.isEmpty()) return Optional.empty();

        List<Tag> resTags = jdbcTemplate.query(
                "select it.tag_id as id, it.item_id, t.key_tag, t.value FROM item_tags as it JOIN tag as t on it.tag_id=t.id"
                + " where it.item_id=?",
                this::mapRowToTag,
                id
        );

        List<Tag> tags = resTags.isEmpty() ?
                null :
                resTags;


        // Now it is 100% safe to call .getFirst()
        LOGGER.info("Got this item from db {}", resItem.getFirst());

        Optional<Item> i = Optional.of(resItem.getFirst());


        i.ifPresent(item -> item.setTags(tags));
        LOGGER.info("Returning this item {}", i);
        return i;
    }

    @Override
    public List<Tag> getTags(){
        return jdbcTemplate.query(
                "select * from tag;",
                this::mapRowToTag);
    }

    @Override
    public String removeTagFromItem(Long itemId, Long tagId){
         var res = jdbcTemplate.update("delete from item_tags where tag_id=? and item_id=?",
                tagId,
                itemId);

         return "Tag deleted from item";
    }
}

record Pair<A, B>(A first, B second) {}
