package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserStorageImpl implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> takeById(Long id) {
        User user = users.get(id);
        return user == null ? Optional.empty() : Optional.of(user);
    }

    @Override
    public Collection<User> takeAll() {
        return users.values();
    }

    @Override
    public boolean delete(Long id) {
        return users.remove(id) != null;
    }
}