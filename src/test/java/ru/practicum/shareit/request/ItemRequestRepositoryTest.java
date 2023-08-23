package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;


import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final EntityManager entityManager;

    private final int from = Integer.parseInt(Constants.PAGE_DEFAULT_FROM);
    private final int size = Integer.parseInt(Constants.PAGE_DEFAULT_SIZE);
    private final Pageable pageable = PageRequest.of(from / size, size);
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    private final User user1 = User.builder()
            .name("user 1")
            .email("net1@net.ru")
            .build();
    private final User user2 = User.builder()
            .name("user 2")
            .email("net2@net.ru")
            .build();
    private final ItemRequest itemRequest1 = ItemRequest.builder()
            .description("itemRequest1 description")
            .requesterId(user2)
            .created(dateTime)
            .build();
    private final Item item1 = Item.builder()
            .name("item name")
            .description("item description")
            .available(true)
            .owner(user1)
            .requestId(itemRequest1.getId())
            .build();


    public void beforeEach() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(itemRequest1);
        entityManager.persist(item1);
    }

    private void checkItemRequest(ItemRequest itemRequest, User user, LocalDateTime dateTime, ItemRequest resultItemRequest) {
        beforeEach();
        assertEquals(itemRequest.getId(), resultItemRequest.getId());
        assertEquals(itemRequest.getDescription(), resultItemRequest.getDescription());
        assertEquals(user.getId(), resultItemRequest.getRequesterId().getId());
        assertEquals(user.getName(), resultItemRequest.getRequesterId().getName());
        assertEquals(user.getEmail(), resultItemRequest.getRequesterId().getEmail());
        assertEquals(dateTime, resultItemRequest.getCreated());
    }

    @Test
    public void shouldGetOne() {
        beforeEach();
        List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user2.getId());

        assertEquals(1, itemsRequest.size());

        ItemRequest resultItemRequest = itemsRequest.get(0);

        checkItemRequest(itemRequest1, user2, dateTime, resultItemRequest);
    }

    @Test
    public void shouldGetZeroIfNotRequests() {
        beforeEach();
        List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user1.getId());

        assertTrue(itemsRequest.isEmpty());
    }

    @Test
    public void shouldGetZeroIfOwner() {
        beforeEach();
        List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdNot(user2.getId(), pageable)
                .get().collect(Collectors.toList());

        assertTrue(itemsRequest.isEmpty());
    }

    @Test
    public void shouldGetOneIfNotOwner() {
        beforeEach();
        List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdNot(user1.getId(), pageable)
                .get().collect(Collectors.toList());

        assertEquals(1, itemsRequest.size());

        ItemRequest resultItemRequest = itemsRequest.get(0);

        checkItemRequest(itemRequest1, user2, dateTime, resultItemRequest);
    }
}