package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(Long id);

    User getUserById(Long id);

    void delete(Long id);
}