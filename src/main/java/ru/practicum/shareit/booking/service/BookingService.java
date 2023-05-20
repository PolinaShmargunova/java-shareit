package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

public interface BookingService {
    FullBookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException;

    FullBookingDto approveBooking(long bookingId, boolean approved, long bookerId) throws BadRequestException;

    FullBookingDto getBooking(long bookingId, long bookerId);

    List<FullBookingDto> getAllBookingsByBookerId(long bookerId, BookingState state, Integer from, Integer size);

    List<FullBookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state, Integer from, Integer size);
}