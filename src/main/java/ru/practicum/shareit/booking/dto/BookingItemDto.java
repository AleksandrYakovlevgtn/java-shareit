package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingItemDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}