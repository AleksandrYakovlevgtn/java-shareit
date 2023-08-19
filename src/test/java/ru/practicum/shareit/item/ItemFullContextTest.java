package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
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
public class ItemFullContextTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;
    private final BookingService bookingService;

    private void checkItemExtendedDto(ItemExtendedDto itemFromController, ItemDto itemDto) {
        assertEquals(itemFromController.getId(), itemDto.getId());
        assertEquals(itemFromController.getName(), itemDto.getName());
        assertEquals(itemFromController.getDescription(), itemDto.getDescription());
        assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
        assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
        assertEquals(itemFromController.getRequestId(), itemDto.getRequestId());
    }

    private void checkItemExtendedDtoBooking(ItemExtendedDto itemFromController,
                                             BookingResponseDto lastBookingResponseDto,
                                             BookingResponseDto nextBookingResponseDto) {
        assertEquals(itemFromController.getLastBooking().getId(), lastBookingResponseDto.getId());
        assertEquals(itemFromController.getLastBooking().getBookerId(), lastBookingResponseDto.getBooker().getId());
        assertEquals(itemFromController.getLastBooking().getStart(), lastBookingResponseDto.getStart());
        assertEquals(itemFromController.getLastBooking().getEnd(), lastBookingResponseDto.getEnd());

        assertEquals(itemFromController.getNextBooking().getId(), nextBookingResponseDto.getId());
        assertEquals(itemFromController.getNextBooking().getBookerId(), nextBookingResponseDto.getBooker().getId());
        assertEquals(itemFromController.getNextBooking().getStart(), nextBookingResponseDto.getStart());
        assertEquals(itemFromController.getNextBooking().getEnd(), nextBookingResponseDto.getEnd());
    }

    private void checkItemDto(ItemDto itemDtoFromController, ItemDto itemDto) {
        assertEquals(itemDtoFromController.getId(), itemDto.getId());
        assertEquals(itemDtoFromController.getName(), itemDto.getName());
        assertEquals(itemDtoFromController.getDescription(), itemDto.getDescription());
        assertEquals(itemDtoFromController.getAvailable(), itemDto.getAvailable());
        assertEquals(itemDtoFromController.getOwnerId(), itemDto.getOwnerId());
        assertEquals(itemDtoFromController.getRequestId(), itemDto.getRequestId());
    }

    @Test
    public void shouldAdd() {
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

        checkItemExtendedDto(itemFromController, itemDto);
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

    @Test
    public void shouldGetFromControllerReturn() {
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

        checkItemExtendedDto(itemFromController1, itemDto1);
        checkItemExtendedDto(itemFromController3, itemDto3);

        List<ItemExtendedDto> itemsFromController2 = itemController.getByOwnerId(
                userDto2.getId(),
                Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

        assertEquals(itemsFromController2.size(), 1);

        ItemExtendedDto itemFromController2 = itemsFromController2.get(0);

        checkItemExtendedDto(itemFromController2, itemDto2);
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

        assertTrue(itemsFromController.isEmpty());
    }

    @Test
    public void shouldHaveBookingDateAndComments() {
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
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto2.getOwnerId(), itemDto2);

        BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2023, 8, 13, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto1 = bookingService.add(userDto2.getId(), bookingRequestDto1);
        bookingController.update(userDto1.getId(), bookingResponseDto1.getId(), true);

        BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2025, 8, 30, 10, 0, 0))
                .end(LocalDateTime.of(2025, 9, 30, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto2 = bookingService.add(userDto2.getId(), bookingRequestDto2);
        bookingController.update(userDto1.getId(), bookingResponseDto2.getId(), true);

        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        itemController.addComment(userDto2.getId(), itemDto1.getId(), commentRequestDto);

        List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(
                userDto1.getId(),
                Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

        assertEquals(itemsFromController.size(), 2);

        ItemExtendedDto itemFromController1 = itemsFromController.get(0);
        ItemExtendedDto itemFromController2 = itemsFromController.get(1);

        assertEquals(itemFromController1.getId(), itemDto1.getId());
        checkItemExtendedDtoBooking(itemFromController1, bookingResponseDto1, bookingResponseDto2);

        List<CommentDto> commentsItem1 = itemFromController1.getComments();

        assertEquals(commentsItem1.size(), 1);
        CommentDto commentDto = commentsItem1.get(0);

        assertEquals(commentDto.getText(), commentRequestDto.getText());
        assertEquals(commentDto.getAuthorName(), userDto2.getName());

        assertEquals(itemFromController2.getId(), itemDto2.getId());
        assertNull(itemFromController2.getLastBooking());
        assertNull(itemFromController2.getNextBooking());

        List<CommentDto> commentsItem2 = itemFromController2.getComments();

        assertTrue(commentsItem2.isEmpty());
    }

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

        checkItemExtendedDto(itemFromController, itemDto);
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

    @Test
    public void shouldRequestByOwnerHaveBookingDateAndComments() {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("user 1")
                .email("net1@net.ru")
                .build();
        userController.add(userDto1);

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("user 2")
                .email("net@net.ru")
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
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto2.getOwnerId(), itemDto2);

        BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2023, 8, 13, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto1 = bookingService.add(userDto2.getId(), bookingRequestDto1);
        bookingController.update(userDto1.getId(), bookingResponseDto1.getId(), true);

        BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2025, 8, 30, 10, 0, 0))
                .end(LocalDateTime.of(2025, 9, 30, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto2 = bookingService.add(userDto2.getId(), bookingRequestDto2);
        bookingController.update(userDto1.getId(), bookingResponseDto2.getId(), true);

        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        itemController.addComment(userDto2.getId(), itemDto1.getId(), commentRequestDto);

        ItemExtendedDto itemFromController1 = itemController.getById(userDto1.getId(), itemDto1.getId());

        assertEquals(itemFromController1.getId(), itemDto1.getId());
        checkItemExtendedDtoBooking(itemFromController1, bookingResponseDto1, bookingResponseDto2);

        List<CommentDto> commentsItem1 = itemFromController1.getComments();

        assertEquals(commentsItem1.size(), 1);
        CommentDto comment = commentsItem1.get(0);

        assertEquals(comment.getText(), commentRequestDto.getText());
        assertEquals(comment.getAuthorName(), userDto2.getName());

        ItemExtendedDto itemFromController2 = itemController.getById(userDto1.getId(), itemDto2.getId());

        assertEquals(itemFromController2.getId(), itemDto2.getId());
        assertNull(itemFromController2.getLastBooking());
        assertNull(itemFromController2.getNextBooking());

        List<CommentDto> commentsItem2 = itemFromController2.getComments();

        assertTrue(commentsItem2.isEmpty());
    }

    @Test
    public void shouldRequestByNoOwnerHaveNotBookingDateAndHaveComments() {
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
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto2.getOwnerId(), itemDto2);

        BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2023, 8, 13, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto1 = bookingService.add(userDto2.getId(), bookingRequestDto1);
        bookingController.update(userDto1.getId(), bookingResponseDto1.getId(), true);

        BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 8, 30, 10, 0, 0))
                .end(LocalDateTime.of(2023, 9, 30, 11, 0, 0))
                .itemId(itemDto1.getId())
                .build();
        BookingResponseDto bookingResponseDto2 = bookingService.add(userDto2.getId(), bookingRequestDto2);
        bookingController.update(userDto1.getId(), bookingResponseDto2.getId(), true);

        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        itemController.addComment(userDto2.getId(), itemDto1.getId(), commentRequestDto);

        ItemExtendedDto itemFromController1 = itemController.getById(userDto2.getId(), itemDto1.getId());

        assertEquals(itemFromController1.getId(), itemDto1.getId());
        assertNull(itemFromController1.getLastBooking());
        assertNull(itemFromController1.getNextBooking());

        List<CommentDto> commentsItem1 = itemFromController1.getComments();

        assertEquals(commentsItem1.size(), 1);
        CommentDto comment = commentsItem1.get(0);

        assertEquals(comment.getText(), commentRequestDto.getText());
        assertEquals(comment.getAuthorName(), userDto2.getName());

        ItemExtendedDto itemFromController2 = itemController.getById(userDto2.getId(), itemDto2.getId());

        assertEquals(itemFromController2.getId(), itemDto2.getId());
        assertNull(itemFromController2.getLastBooking());
        assertNull(itemFromController2.getNextBooking());

        List<CommentDto> commentsItem2 = itemFromController2.getComments();

        assertTrue(commentsItem2.isEmpty());
    }

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

        checkItemExtendedDto(itemFromController, itemDto1);
    }

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

        assertTrue(itemController.getByOwnerId(userDto.getId(),
                        Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                        Integer.parseInt(Constants.PAGE_DEFAULT_SIZE))
                .isEmpty());
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
                .name("Test item 1 TesTs")
                .description("Test item description 1")
                .available(true)
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto1.getOwnerId(), itemDto1);

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Test item 2 TesTs")
                .description("Test item description 2 TesTs")
                .available(false)
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto2.getOwnerId(), itemDto2);

        ItemDto itemDto3 = ItemDto.builder()
                .id(3L)
                .name("Test item 3")
                .description("Test item description 3 TesTs")
                .available(true)
                .ownerId(userDto2.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto3.getOwnerId(), itemDto3);

        ItemDto itemDto4 = ItemDto.builder()
                .id(4L)
                .name("Test item 4")
                .description("Test item description 4")
                .available(true)
                .ownerId(userDto2.getId())
                .requestId(null)
                .build();
        itemController.add(itemDto4.getOwnerId(), itemDto4);

        List<ItemDto> itemsFromController = itemController.search(
                "tEStS",
                Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

        assertEquals(itemsFromController.size(), 2);

        ItemDto itemFromController1 = itemsFromController.get(0);
        ItemDto itemFromController2 = itemsFromController.get(1);

        checkItemDto(itemFromController1, itemDto1);
        checkItemDto(itemFromController2, itemDto3);
    }

    @Test
    public void shouldSearchIfEmpty() {
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

        List<ItemDto> itemsFromController = itemController.search(
                " ",
                Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

        assertTrue(itemsFromController.isEmpty());
    }

    @Test
    public void shouldCreate() {
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
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2023, 8, 13, 11, 0, 0))
                .itemId(itemDto.getId())
                .build();
        BookingResponseDto bookingResponseDto = bookingService.add(userDto2.getId(), bookingRequestDto);
        bookingController.update(userDto1.getId(), bookingResponseDto.getId(), true);

        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        itemController.addComment(userDto2.getId(), itemDto.getId(), commentRequestDto);

        ItemExtendedDto item = itemController.getById(userDto1.getId(), itemDto.getId());

        List<CommentDto> comments = item.getComments();

        assertEquals(comments.size(), 1);
        CommentDto comment = comments.get(0);

        assertEquals(comment.getText(), commentRequestDto.getText());
        assertEquals(comment.getAuthorName(), userDto2.getName());
    }

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
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2050, 8, 14, 11, 0, 0))
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
                .start(LocalDateTime.of(2023, 8, 13, 10, 0, 0))
                .end(LocalDateTime.of(2023, 8, 13, 11, 0, 0))
                .itemId(itemDto.getId())
                .build();
        bookingService.add(userDto2.getId(), bookingRequestDto);

        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");

        BookingException exception = assertThrows(BookingException.class,
                () -> itemController.addComment(userDto2.getId(), itemDto.getId(), commentRequestDto));
        assertEquals("Пользователь не брал вещь в аренду.", exception.getMessage());
    }
}