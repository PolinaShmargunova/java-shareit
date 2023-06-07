package ru.practicum.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.ShortItemDto;
import ru.practicum.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    long id;

    LocalDateTime start;

    LocalDateTime end;

    Long itemId;

    BookingStatus status;

    ShortUserDto booker;

    ShortItemDto item;

}
