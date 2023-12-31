package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.AuthorisationException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userService.getUserById(userId));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long id, CommentRequestDto commentRequestDto) {
        Comment comment = itemMapper.commentRequestDtoToComment(commentRequestDto,
                LocalDateTime.now(),
                userService.getUserById(userId),
                id);

        if (bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(
                id, userId, LocalDateTime.now(), Status.APPROVED).isEmpty()) {
            throw new BookingException("Пользователь не брал вещь в аренду.");
        }
        return itemMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        Item updItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещи не существует."));
        if (!Objects.equals(userId, updItem.getOwner().getId())) {
            throw new AuthorisationException("Изменение вещи доступно только владельцу.");
        }
        if (itemDto.getDescription() != null) {
            updItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            updItem.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            updItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(updItem));
    }

    @Override
    public List<ItemExtendedDto> getByOwnerId(Long userId, Pageable pageable) {
        Page<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable);

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItem = commentRepository.findByItemIdIn(itemIds).stream().collect(Collectors.groupingBy(Comment::getItemId));

        Map<Item, List<Booking>> bookingsByItem = bookingRepository.findByItemIdIn(itemIds).stream().collect(Collectors.groupingBy(Booking::getItem));

        Map<Long, List<CommentDto>> commentDtosByItem = commentDtosByItem(items, commentsByItem);

        Map<Long, List<BookingItemDto>> bookingDtosByItem = bookingDtosByItem(items, bookingsByItem);

        Map<Long, BookingItemDto> lastBookingByItem = new HashMap<>();
        Map<Long, BookingItemDto> nextBookingByItem = new HashMap<>();

        for (Item item : items) {
            if (bookingsByItem.get(item) == null) {
                lastBookingByItem.put(item.getId(), null);
                nextBookingByItem.put(item.getId(), null);
            } else {
                nextBookingByItem.put(item.getId(), bookingDtosByItem.get(item.getId())
                        .stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .findFirst().orElse(null));
                lastBookingByItem.put(item.getId(), bookingDtosByItem.get(item.getId())
                        .stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .findFirst().orElse(null));
            }
        }

        List<ItemExtendedDto> itemExtendedDos = items.stream().map(item -> itemMapper.toItemExtendedDto(item, null, null, null)).collect(Collectors.toList());

        itemExtendedDos.forEach(ItemDto -> {
            ItemDto.setComments(commentDtosByItem.get(ItemDto.getId()));
            ItemDto.setNextBooking(lastBookingByItem.get(ItemDto.getId()));
            ItemDto.setLastBooking(nextBookingByItem.get(ItemDto.getId()));
        });
        return itemExtendedDos;
    }

    @Override
    public ItemExtendedDto getById(Long userId, Long id) {
        Item item = getItemById(id);
        if (!Objects.equals(userId, item.getOwner().getId())) {
            return itemMapper.toItemExtendedDto(item, null, null, addComment(item));
        } else {
            return itemMapper.toItemExtendedDto(item, addLastBooking(item), addNextBooking(item), addComment(item));
        }
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещи не существует."));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text, pageable).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    private BookingItemDto addLastBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                item.getId(), LocalDateTime.now(), Status.APPROVED);

        if (bookings.isEmpty()) {
            return null;
        } else {
            Booking lastBooking = bookings.get(0);
            return itemMapper.bookingToBookingItemDto(lastBooking);
        }
    }

    private BookingItemDto addNextBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                item.getId(), LocalDateTime.now(), Status.APPROVED);

        if (bookings.isEmpty()) {
            return null;
        } else {
            Booking nextBooking = bookings.get(0);
            return itemMapper.bookingToBookingItemDto(nextBooking);
        }
    }

    private List<CommentDto> addComment(Item item) {
        return commentRepository.findByItemId(item.getId()).stream().map(itemMapper::commentToCommentDto).collect(Collectors.toList());
    }

    private Map<Long, List<CommentDto>> commentDtosByItem(Page<Item> items, Map<Long, List<Comment>> commentsByItem) {
        Map<Long, List<CommentDto>> commentDtosByItem = new HashMap<>();
        for (Item item : items) {
            if (commentsByItem.get(item.getId()) == null) {
                commentDtosByItem.put(item.getId(), new ArrayList<>());
            } else {
                commentDtosByItem.put(item.getId(), commentsByItem.get(item.getId())
                        .stream()
                        .map(itemMapper::commentToCommentDto)
                        .collect(Collectors.toList()));
            }
        }
        return commentDtosByItem;
    }

    private Map<Long, List<BookingItemDto>> bookingDtosByItem(Page<Item> items, Map<Item, List<Booking>> bookingsByItem) {
        Map<Long, List<BookingItemDto>> bookingDtosByItem = new HashMap<>();
        for (Item item : items) {
            if (bookingsByItem.get(item) != null) {
                bookingDtosByItem.put(item.getId(), bookingsByItem.get(item)
                        .stream()
                        .filter(Booking -> Booking.getStatus().equals(Status.APPROVED))
                        .map(itemMapper::bookingToBookingItemDto)
                        .sorted(Comparator.comparing(BookingItemDto::getStart))
                        .collect(Collectors.toList()));
            }
        }
        return bookingDtosByItem;
    }
}