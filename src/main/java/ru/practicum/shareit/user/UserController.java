package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    private final UserService userService;

    @Autowired
    public UserController(UserService uService) {
        this.userService = uService;
    }

    @PostMapping
    public UserDto add(@Validated @RequestBody User user) {
        return userService.add(user);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody User user,
                          @PathVariable Long id) {
        return userService.update(user, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}