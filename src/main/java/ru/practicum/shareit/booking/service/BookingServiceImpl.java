package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public FullBookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException, NotFoundException {
        Optional<Item> itemId = itemRepository.findById(dto.getItemId());

        if ((itemId.isEmpty() || userRepository.findById(bookerId).isEmpty())
                || itemId.get().getOwnerId() == bookerId) {
            throw new NotFoundException("Не найдены параметра для создания бронирования");
        }
        if (itemId.get().isAvailable() && dto.getEnd().isAfter(dto.getStart())) {
            return BookingMapper.toFullBookingFromBooking(
                    bookingRepository.save(BookingMapper.toBooking(dto, bookerId, Status.WAITING)),
                    Status.WAITING, itemRepository, userRepository);
        } else {
            throw new BadRequestException();
        }
    }

    @Override
    public FullBookingDto approveBooking(long bookingId, boolean approved, long itemOwnerId)
            throws BadRequestException, NotFoundException {
        try {
            if (itemRepository.findById(bookingRepository.findById(bookingId).get()
                .getItemId()).get().getOwnerId() != itemOwnerId) {
                throw new NotFoundException("Не найден владелец вещи");
            }
        } catch (Exception e) {
            throw new NotFoundException("Не найден владелец вещи");
        }
        if (bookingRepository.findById(bookingId).isPresent()) {
            long bookerId = bookingRepository.findById(bookingId).get().getBookerId();
            BookingDto dto = BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
            dto.setId(bookingId);
            Booking booking;
            Status status;
            if (bookingRepository.findById(bookingId).get().getStatus() == Status.APPROVED && approved) {
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
    public FullBookingDto getBooking(long bookingId, long bookerId) throws NotFoundException {
        Booking booking;
        if (bookingRepository.findById(bookingId).isPresent()) {
            booking = bookingRepository.findById(bookingId).get();
        } else {
            throw new NotFoundException("Несуществующее бронирование");
        }
        if (booking.getBookerId() != bookerId &&
                itemRepository.findById(booking.getItemId()).get().getOwnerId() != bookerId) {
            throw new NotFoundException("Бронирование своей вещи невозможно");
        }
        Status status = booking.getStatus();
        return BookingMapper.toFullBookingFromBooking(booking, status, itemRepository, userRepository);
    }

    @Override
    public List<FullBookingDto> getAllBookingsByBookerId(long bookerId, BookingState state) throws NotFoundException {
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new NotFoundException("Не найден хозяин бронирования");
        }
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerId(bookerId, Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndEndIsBeforeAndStartIsAfter(bookerId, LocalDateTime.now(),
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Override
    public List<FullBookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state) throws NotFoundException {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Не найден владелец вещи");
        }

        switch (state) {
            case ALL:
                return bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.bookingsForItemPast(ownerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.bookingsForItemFuture(ownerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.bookingsForItemCurrent(ownerId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .filter(l -> l.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(l -> BookingMapper.toFullBookingFromBooking(l, l.getStatus(),
                                itemRepository, userRepository))
                        .filter(l -> l.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());

        }
        return null;
    }
}