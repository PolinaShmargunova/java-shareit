package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto, long requesterId) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requesterId(requesterId)
                .created(LocalDateTime.now())
                .build();

    }

    public static GetItemRequestDto toGetItemRequestDto(ItemRequest request) {
        return GetItemRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription())
                .build();
    }
}