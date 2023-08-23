package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {
    private final User user = User.builder()
            .id(1L)
            .name("user 1")
            .email("net1@net.ru")
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user 1")
            .email("net1@net.ru")
            .build();
    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    public void shouldReturnUserDto() {
        UserDto result = userMapper.toUserDto(user);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void shouldReturnNull() {
        UserDto result = userMapper.toUserDto(null);

        assertNull(result);
    }

    @Test
    public void shouldReturnUser() {
        User result = userMapper.toUser(userDto);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    public void shouldReturnNullFromMapper() {
        User result = userMapper.toUser(null);

        assertNull(result);
    }
}