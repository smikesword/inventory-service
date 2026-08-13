    package com.twise.inventory_service.controllers;

    import com.twise.inventory_service.models.Item;
    import com.twise.inventory_service.models.Tag;
    import com.twise.inventory_service.services.InventoryService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Objects;

    @RestController
    @RequestMapping("item")
    public class InventoryController {
        private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);

        private final InventoryService inventoryService;

        public InventoryController(InventoryService inventoryService){
            this.inventoryService = inventoryService;
        }

        @GetMapping("/home")
        public String getHome(){
            LOGGER.info("reached home endpoint");
            return "Hello from root";
        }

        @GetMapping("{id}")
        public ResponseEntity<Item> getItem(@PathVariable String id){
            Item item = inventoryService.getItemById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(item);
        }

        @GetMapping
        public ResponseEntity<Iterable<Item>> getItems(){
            LOGGER.info("fetching items");
            Iterable<Item> items = inventoryService.getAllItems();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(items);
        }

        @PostMapping
        public ResponseEntity<Item> addItem(@RequestBody Item item){
            List<Tag> tags = item.getTags().isEmpty() ? null : item.getTags();
            Item newItem = inventoryService.addItem(item, tags);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(newItem);
        }

        @DeleteMapping("{id}")
        public ResponseEntity<String> deleteItem(@PathVariable String id){
            String res = inventoryService.deleteItemById(id);
            return ResponseEntity.
                    status(HttpStatus.ACCEPTED)
                    .body(res);
        }

        @DeleteMapping("{id}/tags/{tag_id}")
        public ResponseEntity<Void> deleteTagFromItem(@PathVariable String id, @PathVariable String tag_id){
            String res = inventoryService.deleteTagFromItem(id, tag_id);
            if(!Objects.equals(res, "Tag deleted from item")) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        @GetMapping("/tags")
        public ResponseEntity<Iterable<Tag>> getTags(){
            LOGGER.info("fetching tags");
            Iterable<Tag> tags = inventoryService.getAllTags();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(tags);
        }
    }
