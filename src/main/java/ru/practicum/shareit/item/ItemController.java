package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    public List<ItemExtendedDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemExtendedDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long id) {
        return itemService.getById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PostMapping("{id}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long id,
                                 @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, id, commentRequestDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}