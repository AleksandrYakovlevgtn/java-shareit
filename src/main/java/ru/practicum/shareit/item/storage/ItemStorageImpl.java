package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemStorageImpl implements ItemStorage {
    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(++id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public Optional<Item> get(Long id) {
        Item item = items.get(id);
        return item == null ? Optional.empty() : Optional.of(item);
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }
}