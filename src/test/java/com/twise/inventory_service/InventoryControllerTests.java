package com.twise.inventory_service;


import com.twise.inventory_service.models.Item;
import com.twise.inventory_service.models.ItemDoesNotExistsException;
import com.twise.inventory_service.models.Tag;
import com.twise.inventory_service.services.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;


import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class InventoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Autowired
    private JsonMapper jsonMapper;


    @Test
    void whenGetNonExistentItemThrowItemDNException() throws Exception{
        String id = "43";
        given(inventoryService.getItemById(id)).willThrow(ItemDoesNotExistsException.class);
        mockMvc
                .perform(get("/item/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenAddValidItemReturnCreated() throws Exception {
        Item newItem = Item.of("Cat food", 500.00, null, null, 10, "kg");
        Tag tag = Tag.of("type", "cat_food");
        newItem.setTags(List.of(tag));



        mockMvc
                .perform(post("/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated());
    }

}
