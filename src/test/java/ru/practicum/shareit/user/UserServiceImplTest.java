package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User user1 = User.builder()
            .id(1L)
            .name("user 1")
            .email("net1@net.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user 2")
            .email("net2@net.ru")
            .build();
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private UserDto updateUserDto;

    @BeforeEach
    public void beforeEachPatch() {
        updateUserDto = UserDto.builder()
                .id(1L)
                .name("Update test user 1")
                .email("net2@net.ru")
                .build();
    }

    private void checkUserDto(User user, UserDto userDtoFromService) {
        assertEquals(user.getId(), userDtoFromService.getId());
        assertEquals(user.getName(), userDtoFromService.getName());
        assertEquals(user.getEmail(), userDtoFromService.getEmail());
    }

    @Test
    public void shouldGet() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(any())).thenCallRealMethod();

        List<UserDto> usersFromService = userService.getAll();

        assertEquals(2, usersFromService.size());

        UserDto userFromService1 = usersFromService.get(0);
        UserDto userFromService2 = usersFromService.get(1);

        checkUserDto(user1, userFromService1);
        checkUserDto(user2, userFromService2);
        verify(userMapper, times(2)).toUserDto(any());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void shouldGetIfEmpty() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> usersFromService = userService.getAll();

        assertTrue(usersFromService.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void shouldGetFromService() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userMapper.toUserDto(any())).thenCallRealMethod();

        UserDto userFromService = userService.getById(1L);

        checkUserDto(user1, userFromService);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toUserDto(any());
    }

    @Test
    public void shouldThrowExceptionIfUserIdNotFoundWhenSearch() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(999L));
        assertEquals("Пользователя не существует.", exception.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void shouldAdd() {
        when(userMapper.toUserDto(any())).thenCallRealMethod();
        when(userMapper.toUser(any())).thenCallRealMethod();

        userService.add(userMapper.toUserDto(user1));

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    public void shouldUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.update(user1.getId(), updateUserDto);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertEquals(user1.getId(), savedUser.getId());
        assertEquals(updateUserDto.getName(), savedUser.getName());
        assertEquals(updateUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    public void shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(999L, updateUserDto));
        assertEquals("Пользователя не существует.", exception.getMessage());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.delete(user1.getId());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(1L));
        assertEquals("Пользователя не существует.", exception.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldDeleteIfUserIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        userService.delete(999L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(999L));
        assertEquals("Пользователя не существует.", exception.getMessage());
        verify(userRepository, times(1)).deleteById(999L);
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void shouldGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        User userFromService = userService.getUserById(1L);

        assertEquals(user1.getId(), userFromService.getId());
        assertEquals(user1.getName(), userFromService.getName());
        assertEquals(user1.getEmail(), userFromService.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldThrowExceptionIfUserIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(999L));
        assertEquals("Пользователя не существует.", exception.getMessage());
        verify(userRepository, times(1)).findById(any());
    }
}