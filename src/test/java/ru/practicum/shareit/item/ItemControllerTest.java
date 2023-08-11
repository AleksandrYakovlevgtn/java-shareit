package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.exception.AuthorisationException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final BookingService bookingService;

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("user")
                    .email("net@net.ru")
                    .build();
            userController.add(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(
                    userDto.getId(),
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(itemsFromController.size(), 1);

            ItemExtendedDto itemFromController = itemsFromController.get(0);

            assertEquals(itemFromController.getId(), itemDto.getId());
            assertEquals(itemFromController.getName(), itemDto.getName());
            assertEquals(itemFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemFromController.getRequestId(), itemDto.getRequestId());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdNotFound() {
            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(10L)
                    .requestId(null)
                    .build();
            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.add(10L, itemDto));
            assertEquals("Пользователя не существует.", exception.getMessage());
        }
    }

    @Nested
    class GetByOwner {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("user 1")
                    .email("net1@net.ru")
                    .build();
            userController.add(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("user 2")
                    .email("net2@net.ru")
                    .build();
            userController.add(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("item 3")
                    .description("Test item description 3")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto3.getOwnerId(), itemDto3);

            List<ItemExtendedDto> itemsFromController1 = itemController.getByOwnerId(
                    userDto1.getId(),
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(itemsFromController1.size(), 2);

            ItemExtendedDto itemFromController1 = itemsFromController1.get(0);
            ItemExtendedDto itemFromController3 = itemsFromController1.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getName(), itemDto1.getName());
            assertEquals(itemFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController1.getRequestId(), itemDto1.getRequestId());

            assertEquals(itemFromController3.getId(), itemDto3.getId());
            assertEquals(itemFromController3.getName(), itemDto3.getName());
            assertEquals(itemFromController3.getDescription(), itemDto3.getDescription());
            assertEquals(itemFromController3.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemFromController3.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemFromController3.getRequestId(), itemDto3.getRequestId());

            List<ItemExtendedDto> itemsFromController2 = itemController.getByOwnerId(
                    userDto2.getId(),
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(itemsFromController2.size(), 1);

            ItemExtendedDto itemFromController2 = itemsFromController2.get(0);

            assertEquals(itemFromController2.getId(), itemDto2.getId());
            assertEquals(itemFromController2.getName(), itemDto2.getName());
            assertEquals(itemFromController2.getDescription(), itemDto2.getDescription());
            assertEquals(itemFromController2.getAvailable(), itemDto2.getAvailable());
            assertEquals(itemFromController2.getOwnerId(), itemDto2.getOwnerId());
            assertEquals(itemFromController2.getRequestId(), itemDto2.getRequestId());
        }
    }

    @Test
    public void shouldGetIfEmpty() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("net@net.ru")
                .build();
        userController.add(userDto);

        List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(
                userDto.getId(),
                Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

        assertEquals(itemsFromController.size(), 0);
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("user")
                    .email("net@net.ru")
                    .build();
            userController.add(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            ItemExtendedDto itemFromController = itemController.getById(userDto.getId(), itemDto.getId());

            assertEquals(itemFromController.getId(), itemDto.getId());
            assertEquals(itemFromController.getName(), itemDto.getName());
            assertEquals(itemFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemFromController.getRequestId(), itemDto.getRequestId());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("user")
                    .email("net@net.ru")
                    .build();
            userController.add(userDto);

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getById(userDto.getId(), 10L));
            assertEquals("Вещи не существует.", exception.getMessage());
        }

        @Nested
        class Update {
            @Test
            public void shouldUpdate() {
                UserDto userDto = UserDto.builder()
                        .id(1L)
                        .name("user")
                        .email("net@net.ru")
                        .build();
                userController.add(userDto);

                ItemDto itemDto1 = ItemDto.builder()
                        .id(1L)
                        .name("item 1")
                        .description("Test item description 1")
                        .available(true)
                        .ownerId(userDto.getId())
                        .requestId(null)
                        .build();
                itemController.add(itemDto1.getOwnerId(), itemDto1);

                ItemDto itemDto2 = ItemDto.builder()
                        .id(2L)
                        .name("Update item 1")
                        .description("Update test item description 1")
                        .available(false)
                        .ownerId(userDto.getId())
                        .requestId(null)
                        .build();
                itemController.update(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2);

                ItemExtendedDto itemFromController = itemController.getById(userDto.getId(), itemDto1.getId());

                assertEquals(itemFromController.getId(), itemDto1.getId());
                assertEquals(itemFromController.getName(), itemDto2.getName());
                assertEquals(itemFromController.getDescription(), itemDto2.getDescription());
                assertEquals(itemFromController.getAvailable(), itemDto2.getAvailable());
                assertEquals(itemFromController.getOwnerId(), itemDto2.getOwnerId());
                assertEquals(itemFromController.getRequestId(), itemDto2.getRequestId());
            }

            @Test
            public void shouldThrowExceptionIfItemOwnerIdForbidden() {
                UserDto userDto1 = UserDto.builder()
                        .id(1L)
                        .name("user 1")
                        .email("net1@net.ru")
                        .build();
                userController.add(userDto1);

                UserDto userDto2 = UserDto.builder()
                        .id(2L)
                        .name("user 2")
                        .email("net2@net.ru")
                        .build();
                userController.add(userDto2);

                ItemDto itemDto1 = ItemDto.builder()
                        .id(1L)
                        .name("item 1")
                        .description("Test item description 1")
                        .available(true)
                        .ownerId(userDto1.getId())
                        .requestId(null)
                        .build();
                itemController.add(itemDto1.getOwnerId(), itemDto1);

                ItemDto itemDto2 = ItemDto.builder()
                        .id(2L)
                        .name("Update item 1")
                        .description("Update test item description 1")
                        .available(false)
                        .ownerId(userDto2.getId())
                        .requestId(null)
                        .build();

                AuthorisationException exception = assertThrows(AuthorisationException.class, () -> itemController.update(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2));
                assertEquals("Изменение вещи доступно только владельцу.", exception.getMessage());

                ItemExtendedDto itemFromController = itemController.getById(userDto1.getId(), itemDto1.getId());

                assertEquals(itemFromController.getId(), itemDto1.getId());
                assertEquals(itemFromController.getName(), itemDto1.getName());
                assertEquals(itemFromController.getDescription(), itemDto1.getDescription());
                assertEquals(itemFromController.getAvailable(), itemDto1.getAvailable());
                assertEquals(itemFromController.getOwnerId(), itemDto1.getOwnerId());
                assertEquals(itemFromController.getRequestId(), itemDto1.getRequestId());
            }
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("user")
                    .email("net@net.ru")
                    .build();
            userController.add(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .requestId(null)
                    .build();
            itemController.add(userDto.getId(), itemDto);

            itemController.delete(itemDto.getId());

            assertEquals(itemController.getByOwnerId(userDto.getId(),
                            Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                            Integer.parseInt(Constants.PAGE_DEFAULT_SIZE)).size(),
                    0);
        }

        @Test
        public void shouldDeleteIfItemIdNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("user")
                    .email("net@net.ru")
                    .build();
            userController.add(userDto);

            assertThrows(EmptyResultDataAccessException.class, () -> itemController.delete(10L));

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getById(userDto.getId(), 10L));
            assertEquals("Вещи не существует.", exception.getMessage());
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldSearch() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("user 1")
                    .email("net1@net.ru")
                    .build();
            userController.add(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("user 2")
                    .email("net2@net.ru")
                    .build();
            userController.add(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("item 1")
                    .description("Test item description 1 tEStS")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("item 2")
                    .description("Test item description 2 tEStS")
                    .available(false)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("item 3")
                    .description("Test item description 3 tEStS")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto3.getOwnerId(), itemDto3);

            ItemDto itemDto4 = ItemDto.builder()
                    .id(4L)
                    .name("item 4")
                    .description("Test item description 4")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto4.getOwnerId(), itemDto4);

            List<ItemDto> itemsFromController = itemController.search(
                    "TesTs",
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(itemsFromController.size(), 2);

            ItemDto itemFromController1 = itemsFromController.get(0);
            ItemDto itemFromController2 = itemsFromController.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getName(), itemDto1.getName());
            assertEquals(itemFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController1.getRequestId(), itemDto1.getRequestId());

            assertEquals(itemFromController2.getId(), itemDto3.getId());
            assertEquals(itemFromController2.getName(), itemDto3.getName());
            assertEquals(itemFromController2.getDescription(), itemDto3.getDescription());
            assertEquals(itemFromController2.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemFromController2.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemFromController2.getRequestId(), itemDto3.getRequestId());
        }

        @Test
        public void shouldSearchIfEmpty() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("t-r@ya.ru")
                    .build();
            userController.add(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            List<ItemDto> itemsFromController = itemController.search(
                    " ",
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(itemsFromController.size(), 0);
        }
    }

    @Nested
    class Comment {
        @Test
        public void shouldThrowExceptionIfNoBooking() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("user 1")
                    .email("net1@net.ru")
                    .build();
            userController.add(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("user 2")
                    .email("net2@net.ru")
                    .build();
            userController.add(userDto2);

            CommentRequestDto commentRequestDto = new CommentRequestDto("comment");

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(), itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал вещь в аренду.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfBookingNotFinished() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("user 1")
                    .email("net1@net.ru")
                    .build();
            userController.add(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("user 2")
                    .email("net2@net.ru")
                    .build();
            userController.add(userDto2);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2050, 3, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingService.add(userDto2.getId(), bookingRequestDto);
            bookingController.update(userDto1.getId(), bookingResponseDto.getId(), true);

            CommentRequestDto commentRequestDto = new CommentRequestDto("comment");

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(), itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал вещь в аренду.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfBookingNotApproved() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("user 1")
                    .email("net1@net.ru")
                    .build();
            userController.add(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .requestId(null)
                    .build();
            itemController.add(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("user 2")
                    .email("net2@net.ru")
                    .build();
            userController.add(userDto2);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingService.add(userDto2.getId(), bookingRequestDto);

            CommentRequestDto commentRequestDto = new CommentRequestDto("comment");

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(), itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал вещь в аренду.", exception.getMessage());
        }
    }
}