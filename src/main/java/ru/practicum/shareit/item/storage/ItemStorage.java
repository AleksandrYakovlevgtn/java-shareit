package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item);

    void delete(Long id);

    Optional<Item> get(Long id);

    Collection<Item> getAll();
}