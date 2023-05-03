package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(ItemDto dto, User owner) throws NotFoundException;

    ItemDto patchItem(ItemDto dto, User owner, long itemId) throws NotFoundException;

    ItemDto getItem(long itemId, long ownerId) throws NotFoundException;

    List<ItemDto> getAllItemsByOwner(long ownerId) throws NotFoundException;

    List<ItemDto> searchItem(String text, long ownerId) throws NotFoundException;
}