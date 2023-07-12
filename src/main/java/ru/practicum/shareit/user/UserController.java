package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService uService;

    @Autowired
    public UserController(UserService uService) {
        this.uService = uService;
    }

    @PostMapping
    public UserDto add(@Validated @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка." + errors.getAllErrors());
        } else {
            return uService.add(user);
        }
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return uService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return uService.get(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody User user
            , @PathVariable Long id
            , Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("Произошла ошибка." + errors.getAllErrors());
        } else {
            return uService.update(user, id);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        uService.delete(id);
    }
}