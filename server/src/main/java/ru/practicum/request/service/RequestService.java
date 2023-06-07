package ru.practicum.request.service;

import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> read(Long userId);

    ItemRequestDto read(Long userId, Long itemId);

    List<ItemRequestDto> readAll(Long userId, int from, int size);

    ItemRequest getItemRequestById(Long requestId);

}