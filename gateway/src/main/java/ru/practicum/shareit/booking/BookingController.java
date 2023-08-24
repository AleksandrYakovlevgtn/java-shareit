package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
										  @PathVariable Long id) {
		return bookingClient.getById(userId, id);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByBookerId(
			@RequestHeader(Constants.HEADER_USER_ID) Long userId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
			@RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
		BookingState state = BookingState.stringToState(stateParam).orElseThrow(
				() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getAllByBookerId(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwnerId(
			@RequestHeader(Constants.HEADER_USER_ID) Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM, required = false) @PositiveOrZero Integer from,
			@RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE, required = false) @Positive Integer size) {
		BookingState stateEnum = BookingState.stringToState(state).orElseThrow(
				() -> new IllegalArgumentException("Unknown state: " + state));
		return bookingClient.getAllByOwnerId(userId, stateEnum, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
									  @Valid @RequestBody BookItemRequestDto bookingRequestDto) {
		return bookingClient.add(userId, bookingRequestDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> update(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
										 @PathVariable Long id,
										 @RequestParam() Boolean approved) {
		return bookingClient.update(userId, id, approved);
	}
}