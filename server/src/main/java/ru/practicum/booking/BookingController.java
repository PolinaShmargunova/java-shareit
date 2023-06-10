package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.service.BookingService;
import ru.practicum.booking.service.BookingStateMapper;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(header) Long userId,
                             @RequestBody BookingDto bookingDto) {
        log.debug("Create");
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto read(@RequestHeader(header) Long userId,
                           @PathVariable long bookingId) {
        log.debug("Read({})", bookingId);
        return bookingService.read(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> readAll(@RequestHeader(header) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.debug("ReadAll for userId:{}.", userId);
        return bookingService.readAll(userId, BookingStateMapper.toBookingState(state), from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(header) Long userId,
                             @PathVariable long bookingId,
                             @RequestParam Boolean approved) {
        log.debug("Update({})", bookingId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@RequestHeader(header) Long userId,
                       @PathVariable long bookingId) {
        log.debug("Delete({})", bookingId);
        bookingService.delete(userId, bookingId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> readForOwner(@RequestHeader(header) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.debug("ReadForOwner()");
        return bookingService.readForOwner(userId, BookingStateMapper.toBookingState(state), from, size);
    }
}
