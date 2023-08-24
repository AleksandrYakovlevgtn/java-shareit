package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable Long id) {
        return userClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @Valid
            @RequestBody UserDto userDto) {
        return userClient.add(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody UserDto userDto) {
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {
        userClient.deleteUser(id);
    }
}