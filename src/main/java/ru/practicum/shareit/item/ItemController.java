package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService iService;

    public ItemController(ItemService iService) {
        this.iService = iService;
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return iService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return iService.get(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return iService.search(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto,
                       Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка." + errors.getAllErrors());
        } else {
            return iService.add(itemDto, userId);
        }
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto
            , @PathVariable Long id
            , @RequestHeader("X-Sharer-User-Id") Long userId
            , Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка." + errors.getAllErrors());
        } else {
            return iService.update(itemDto, id, userId);
        }
    }
}