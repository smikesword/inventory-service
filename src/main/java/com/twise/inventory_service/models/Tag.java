package com.twise.inventory_service.models;

public record Tag(
        Long id,
        Long itemId,
        String key,
        String value
) {
    public static Tag of(
        String key,
        String value
    ) {
        return new Tag( null,null, key, value);
    }
}
