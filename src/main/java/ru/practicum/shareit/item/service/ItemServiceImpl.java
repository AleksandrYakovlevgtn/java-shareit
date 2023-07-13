package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.*;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemServiceImpl(UserStorage userStorage, ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = userStorage.takeById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id: " + userId));
        Item item = createItem(itemDto);
        item.setOwner(user);
        itemStorage.add(item);
        return createItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemStorage.get(id).orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        if (item.getOwner() != null && !item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У данного пользователя нет вещи.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemStorage.update(item);
        return createItemDto(item);
    }

    @Override
    public ItemDto get(Long id) {
        Item item = itemStorage.get(id).orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        return createItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> items = itemStorage.getAll().stream().filter(item -> item.getOwner() != null && userId.equals(item.getOwner().getId())).map(ItemMapper::createItemDto).collect(Collectors.toList());
        /*for (Item item : itemStorage.getAll()) {
            if (item.getOwner() != null && userId.equals(item.getOwner().getId())) {
                items.add(createItemDto(item));
            }
        }*/
        return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> founded = new ArrayList<>();
        if (text.isBlank()) {
            return founded;
        }
        for (Item item : itemStorage.getAll()) {
            if (found(text, item)) {
                founded.add(createItemDto(item));
            }
        }
        return founded;
    }

    private Boolean found(String text, Item item) {
        return (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable();
    }
}