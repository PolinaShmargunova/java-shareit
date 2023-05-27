package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addBooking() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 2L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));


        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(newUser));
        assertThrows(NullPointerException.class, () -> bookingService.addBooking(dto, bookerId));
    }

    @Test
    void testAddBookingItemUnavailable() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        long bookerId = 2L;

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setAvailable(false);
        item.setDescription("A description");
        item.setOwnerId(bookerId);
        itemRepository.save(item);

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(dto, bookerId);
        });
    }

    @Test
    void addBooking_whenItemIsNotAvailable() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(owner.getId());
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        item.setAvailable(false);
        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(dto, owner.getId());
        });
    }

    @Test
    void addBooking_whenEndDateIsBeforeStartDate() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(owner.getId());
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        item.setAvailable(true);
        dto.setEnd(LocalDateTime.now().plusDays(-1));
        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(dto, owner.getId());
        });
    }

    @Test
    void addBooking_whenBookerIdIsInvalid() {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(owner.getId());
        itemRepository.save(item);

        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(dto, owner.getId());

            given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
            assertThrows(BadRequestException.class, () -> {
                bookingService.addBooking(dto, owner.getId());
            });
        });
    }

    @Test
    void approveBooking() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));


        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        assertThrows(NullPointerException.class, () -> bookingService.approveBooking(1, true, itemId));
    }

    @Test
    void getBooking() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);


        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        assertThrows(NoSuchElementException.class, () -> bookingService.getBooking(1, bookerId));
    }

    @Test
    public void getBookingWithDifferentBookerAndOwnerIds() {
        long bookingId = 1L;
        long bookerId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookerId(3L);
        Item item = new Item();
        item.setId(4L);
        item.setOwnerId(5L);

        Optional<Booking> optionalBooking = Optional.of(booking);

        when(bookingRepository.findById(bookingId)).thenReturn(optionalBooking);
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookingId, bookerId));

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(itemRepository, times(1)).findById(booking.getItemId());
    }

    @Test
    void getBookingNotFound() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);


        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, bookerId));
    }

    @Test
    void getAllBookings() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(newUser));


        when(bookingRepository.findAllByBookerId(anyLong(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));

        when(bookingRepository.findByBookerIdAndEndAfter(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        when(bookingRepository.findByBookerIdAndEndIsBeforeAndStartIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));

        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.ALL, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.PAST, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.FUTURE, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.CURRENT, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.WAITING, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void getAllBookingsNotFoundUser() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1,
                BookingState.ALL, 0, 10));
    }

    @Test
    void getAllBookingsByItems() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(newUser));


        when(bookingRepository.bookingsForItem(anyLong(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));

        when(bookingRepository.bookingsForItemPast(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        when(bookingRepository.bookingsForItemFuture(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));
        when(bookingRepository.bookingsForItemCurrent(anyLong(), any(), any()))
                .thenReturn(List.of(BookingMapper.toBooking(dto, bookerId, Status.WAITING)));

        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.ALL, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.PAST, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.FUTURE, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.CURRENT, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.WAITING, 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void getAllBookingsByItemsNotFoundUser() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        long bookerId = 1L;

        BookingDto dto = new BookingDto(1, itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void fullBookingTest() {
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(1, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, 1);
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.now(), 1, 1, Status.WAITING);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByItemsByOwnerId(1,
                BookingState.REJECTED, 0, 10));
    }

    @Test
    void bookingMapperTest() {
        User newUser = new User(1, "test", "test@test.com");
        ItemDto itemDto = new ItemDto(1, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, 1);
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.now(), 1, 1, Status.WAITING);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(newUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertEquals(1,
                BookingMapper.toFullBookingFromBooking(booking, Status.WAITING, itemRepository, userRepository).getId());
    }
}
