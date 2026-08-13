package com.twise.inventory_service.models;

public class ItemDoesNotExistsException extends RuntimeException{
    public ItemDoesNotExistsException(Long id){super("Item with id " + id + " does not exist");}
}
