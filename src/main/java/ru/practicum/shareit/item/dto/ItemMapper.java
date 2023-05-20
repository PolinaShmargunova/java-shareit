package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemMapper {

    public static GetItemDto toGetItemDto(Item item, List<Booking> bookings,
                                          List<Comment> comments) {

        GetItemDto getItemDto = new GetItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );

        if (bookings != null && !bookings.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            Optional<Booking> maybeLast = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));

            maybeLast.ifPresent(getItemDto::setLastBooking);

            Optional<Booking> maybeNext = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                    .min(Comparator.comparing(Booking::getStart));

            maybeNext.ifPresent(getItemDto::setNextBooking);
        }

        getItemDto.setComments(comments);

        return getItemDto;
    }

    public static GetItemDto toGetItemDto(Item item, Booking last, Booking near,
                                          List<Comment> comments) {
        return new GetItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                last,
                near,
                comments
        );
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .requestId(item.getRequestId())
                .build();

    }

    public static Item toItem(ItemDto dto, long owner) {
        return new Item(dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                owner,
                dto.getRequestId()
        );
    }
}