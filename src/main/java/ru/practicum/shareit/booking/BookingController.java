package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;


    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByOwnerId(userId, bookingService.checkStateValid(state));
    }

    @GetMapping("/{id}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByBookerId(userId, bookingService.checkStateValid(state));
    }

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.add(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long id,
                                     @RequestParam() Boolean approved) {
        return bookingService.update(userId, id, approved);
    }
}