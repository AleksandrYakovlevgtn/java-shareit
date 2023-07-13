package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long id, Long userId);

    ItemDto get(Long id);

    Collection<ItemDto> getAll(Long userId);

    List<ItemDto> search(String text);
}