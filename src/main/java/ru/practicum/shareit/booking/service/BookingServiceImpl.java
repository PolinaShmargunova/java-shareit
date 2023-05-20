package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public FullBookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException {
        Optional<Item> itemIdDatabase = itemRepository.findById(dto.getItemId());

        if ((itemIdDatabase.isEmpty() || userRepository.findById(bookerId).isEmpty())
                || itemIdDatabase.get().getOwnerId() == bookerId) {
            throw new NotFoundException("Не найдены параметра для создания бронирования");
        }
        if (itemIdDatabase.get().isAvailable()
                && dto.getEnd().isAfter(dto.getStart())) {
            return BookingMapper.toFullBookingFromBooking(
                    bookingRepository.save(BookingMapper.toBooking(dto, bookerId, Status.WAITING)),
                    Status.WAITING, itemRepository, userRepository);
        } else {
            throw new BadRequestException();
        }
    }

    @Override
    public FullBookingDto approveBooking(long bookingId, boolean approved, long itemOwnerId)
            throws BadRequestException {
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (itemRepository.findById(bookingIdDatabase.get()
                .getItemId()).get().getOwnerId() != itemOwnerId) {
            throw new NotFoundException("Не найден владелец вещи");
        }
        if (bookingIdDatabase.isPresent()) {
            long bookerId = bookingIdDatabase.get().getBookerId();
            BookingDto dto = BookingMapper.toBookingDto(bookingIdDatabase.get());
            dto.setId(bookingId);
            Booking booking;
            Status status;
            if (bookingIdDatabase.get().getStatus() == Status.APPROVED && approved) {
                throw new BadRequestException();
            }
            if (approved) {
                status = Status.APPROVED;
            } else {
                status = Status.REJECTED;
            }
            booking = BookingMapper.toBooking(dto, bookerId, status);

            return BookingMapper.toFullBookingFromBooking(bookingRepository.save(booking), status,
                    itemRepository, userRepository);
        } else {
            throw new NotFoundException("Несуществующее бронирование");
        }
    }

    @Override
    public FullBookingDto getBooking(long bookingId, long bookerId) {
        Booking booking;
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (bookingIdDatabase.isPresent()) {
            booking = bookingIdDatabase.get();
        } else {
            throw new NotFoundException("Такого бронирования не существует");
        }
        if (booking.getBookerId() != bookerId &&
                itemRepository.findById(booking.getItemId()).get().getOwnerId() != bookerId) {
            throw new NotFoundException("");
        }
        Status status = booking.getStatus();
        return BookingMapper.toFullBookingFromBooking(booking, status, itemRepository, userRepository);
    }

    @Override
    public List<FullBookingDto> getAllBookingsByBookerId(long bookerId, BookingState state,
                                                         Integer from, Integer size) {
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new NotFoundException("Не найден хозяин бронирования");
        }
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerId(bookerId,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndEndIsBeforeAndStartIsAfter(bookerId, LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            default:
                return null;

        }

    }

    @Override
    public List<FullBookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state,
                                                              Integer from, Integer size) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Не найден владелец вещи");
        }
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {

            case ALL:
                return bookingRepository.bookingsForItem(ownerId,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.bookingsForItemPast(ownerId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.bookingsForItemFuture(ownerId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.bookingsForItemCurrent(ownerId, LocalDateTime.now(),
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.bookingsForItem(ownerId,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .filter(l -> l.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.bookingsForItem(ownerId,
                                pageRequest)
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .filter(l -> l.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());

        }
        return null;
    }
}