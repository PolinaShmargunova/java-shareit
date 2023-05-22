package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingServiceImpl bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    public void addBooking_ValidData_Success() throws BadRequestException, NotFoundException {
        long bookerId = 1;
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);

        when(itemRepository.findById(dto.getItemId())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(new User()));
        FullBookingDto result = bookingService.addBooking(dto, bookerId);

        assertNotNull(result);
    }

    @Test
    public void approveBooking_ValidData_Success() throws BadRequestException, NotFoundException {
        long bookingId = 1;
        long itemOwnerId = 2;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(new Booking()));
        when(itemRepository.findById(itemOwnerId)).thenReturn(Optional.of(new Item()));

        FullBookingDto result = bookingService.approveBooking(bookingId, true, itemOwnerId);
        assertNotNull(result);

    }

    @Test
    public void getBooking_ValidData_Success() {
        long bookingId = 1;
        long bookerId = 2;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(new Booking()));
        when(itemRepository.findById(bookerId)).thenReturn(Optional.of(new Item()));

        FullBookingDto result = bookingService.getBooking(bookingId, bookerId);
        assertNotNull(result);
    }

    @Test
    public void getAllBookingsByBookerId_ValidData_Success() {
        long bookerId = 1;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerId(eq(bookerId), any(PageRequest.class)))
                .thenReturn(Arrays.asList(new Booking(), new Booking()));

        List<FullBookingDto> result = bookingService.getAllBookingsByBookerId(bookerId, state, from, size);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getAllBookingByItemsByOwnerId_ValidData_Success() {
        long ownerId = 1;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.bookingsForItem(eq(ownerId), any(PageRequest.class)))
                .thenReturn(Arrays.asList(new Booking(), new Booking()));

        List<FullBookingDto> result = bookingService.getAllBookingByItemsByOwnerId(ownerId, state, from, size);
        assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    public void getAllBookingByItemsByOwnerId_InvalidData_ThrowsException() {
        long ownerId = 1;
        BookingState state = BookingState.WAITING;
        int from = 0;
        int size = 10;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingByItemsByOwnerId(ownerId, state, from, size)
        );
    }
}


