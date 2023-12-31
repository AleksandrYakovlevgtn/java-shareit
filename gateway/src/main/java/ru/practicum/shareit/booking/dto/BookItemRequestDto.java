package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.valid.StartBeforeEndDateValid;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemRequestDto {
    long itemId;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
}
