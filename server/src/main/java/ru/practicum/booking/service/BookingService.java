package ru.practicum.booking.service;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto read(long userId, long bookingId);

    Collection<BookingDto> readAll(long userId, BookingState state, int from, int size);

    BookingDto updateStatus(long userId, long bookingId, boolean approved);

    void delete(long userId, long bookingId);

    Collection<BookingDto> readForOwner(long userId, BookingState state, int from, int size);

}