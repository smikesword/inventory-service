package com.twise.inventory_service.controllers;

import com.twise.inventory_service.models.ItemDoesNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryControllerAdvice {

    @ExceptionHandler(ItemDoesNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String itemNotFoundHandler(ItemDoesNotExistsException ex) {
        return ex.getMessage();
    }
}
