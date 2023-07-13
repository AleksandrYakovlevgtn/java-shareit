package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto add(User user);

    UserDto update(User user, long id);

    void delete(Long id);

    UserDto get(Long id);

    List<UserDto> getAll();
}