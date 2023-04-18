package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto dto, long ownerId) throws NotFoundException;

    ItemDto patchItem(ItemDto dto,long ownerId, long itemId) throws NotFoundException;

    ItemDto getItem(long itemId, long ownerId) throws NotFoundException;

    List<ItemDto> getAllItemsByOwner(long ownerId) throws NotFoundException;

    List<ItemDto> searchItem(String text, long ownerId) throws NotFoundException;
}