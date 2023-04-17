package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item toItem(ItemDto dto, User owner) {
        return new Item(dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                owner,
                null
        );
    }
}