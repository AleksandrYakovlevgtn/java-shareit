package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        return itemClient.getByOwnerId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                          @PathVariable Long id) {
        return itemClient.getById(userId, id);
    }

    @Validated
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                      @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                         @PathVariable Long id,
                                         @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Constants.HEADER_USER_ID) long userId,
                                             @PathVariable long id,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, id, commentDto);
    }
}