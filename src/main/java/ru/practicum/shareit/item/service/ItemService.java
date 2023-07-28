package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto add(Long userId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long id, CommentRequestDto commentRequestDto);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    List<ItemExtendedDto> getByOwnerId(Long userId);

    ItemExtendedDto getById(Long userId, Long id);

    Item getItemById(Long id);

    void delete(Long id);

    List<ItemDto> search(String text);
}