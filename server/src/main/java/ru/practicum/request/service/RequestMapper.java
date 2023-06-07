package ru.practicum.request.service;

import ru.practicum.item.model.Item;
import ru.practicum.item.service.ItemMapper;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.model.ItemRequest;

import java.util.Collection;

public class RequestMapper {

    private RequestMapper() {
    }

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest, Collection<Item> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(ItemMapper.toItemDto(items));
        return itemRequestDto;
    }

    public static ItemRequest toRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }
}

