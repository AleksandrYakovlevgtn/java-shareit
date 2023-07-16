package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService iService) {
        this.itemService = iService;
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemDto, id, userId);
    }
}