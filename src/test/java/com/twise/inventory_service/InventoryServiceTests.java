package com.twise.inventory_service;

import com.twise.inventory_service.models.ItemDoesNotExistsException;
import com.twise.inventory_service.repositories.JdbcInventoryRepository;
import com.twise.inventory_service.services.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class InventoryServiceTests {

    @Mock
    private JdbcInventoryRepository jdbcInventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void whenItemDoesNotExistThrowEx(){
        var itemId = 43L;
        when(jdbcInventoryRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> inventoryService.getItemById(String.valueOf(itemId)))
                .isInstanceOf(ItemDoesNotExistsException.class)
                .hasMessage("Item with id " + itemId + " does not exist");

        verify(jdbcInventoryRepository, times(1)).findById(43L);
    }
}
