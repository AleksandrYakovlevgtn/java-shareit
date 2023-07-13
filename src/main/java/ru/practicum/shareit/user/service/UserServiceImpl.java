package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto add(User user) {
        if (!checkEmail(user.getId(), user.getEmail())) {
            log.error("Email: " + user.getEmail() + " занят!");
        }
        return createUserDto(userStorage.add(user));
    }

    @Override
    public UserDto update(User user, long id) {
        user.setId(id);
        User updated = userStorage.takeById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с таким id: " + id));
        if (user.getName() != null) {
            updated.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(updated.getEmail())) {
            checkEmail(user.getId(), user.getEmail());
            updated.setEmail(user.getEmail());
        }
        return createUserDto(userStorage.update(updated));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public UserDto get(Long id) {
        User user = userStorage.takeById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с таким id: " + id));
        return createUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userStorage.takeAll()) {
            users.add(createUserDto(user));
        }
        return users;
    }

    public boolean checkEmail(Long id, String email) {
        for (User user : userStorage.takeAll()) {
            if (user.getEmail().equals(email) && !(user.getId().equals(id))) {
                throw new NotUniqueException("Email: " + email + " занят!");
            }
        }
        return true;
    }
}