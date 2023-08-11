package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(
            @RequestHeader(Constants.headerUserId) Long userId,
            @Valid @RequestBody ItemRequestAddDto itemRequestCreateDto) {
        return itemRequestService.add(userId, itemRequestCreateDto);
    }

    @GetMapping("/{id}")
    public ItemRequestExtendedDto getById(
            @RequestHeader(Constants.headerUserId) Long userId,
            @PathVariable Long id) {
        return itemRequestService.getById(userId, id);
    }

    @GetMapping
    public List<ItemRequestExtendedDto> getByRequesterId(
            @RequestHeader(Constants.headerUserId) Long userId) {
        return itemRequestService.getByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestExtendedDto> getAll(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        return itemRequestService.getAll(userId, PageRequest.of(from / size, size));
    }
}