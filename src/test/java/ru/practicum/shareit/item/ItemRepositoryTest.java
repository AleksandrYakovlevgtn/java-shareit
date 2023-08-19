package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;
    private final int from = Integer.parseInt(Constants.PAGE_DEFAULT_FROM);
    private final int size = Integer.parseInt(Constants.PAGE_DEFAULT_SIZE);
    private final Pageable pageable = PageRequest.of(from / size, size);
    private final User user1 = User.builder()
            .name("user 1")
            .email("net1@net.ru")
            .build();
    private final User user2 = User.builder()
            .name("user 2")
            .email("net2@net.ru")
            .build();
    private final Item item1 = Item.builder()
            .name("item1")
            .description("search1 description ")
            .available(true)
            .owner(user1)
            .build();
    private final Item item2 = Item.builder()
            .name("item2")
            .description("Search1 description")
            .available(true)
            .owner(user2)
            .build();
    private final Item item3 = Item.builder()
            .name("item3")
            .description("item3 description")
            .available(false)
            .owner(user1)
            .build();

    public void beforeEach() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
    }

    private void checkItem(Item item1, Item item2) {
        assertEquals(item1.getId(), item2.getId());
        assertEquals(item1.getName(), item2.getName());
        assertEquals(item1.getDescription(), item2.getDescription());
        assertEquals(item1.getAvailable(), item2.getAvailable());
        assertEquals(item1.getOwner().getId(), item2.getOwner().getId());
        assertEquals(item1.getOwner().getName(), item2.getOwner().getName());
        assertEquals(item1.getOwner().getEmail(), item2.getOwner().getEmail());
    }

    @Test
    public void shouldGetTwoItems() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(user1.getId(), pageable)
                .get()
                .collect(Collectors.toList());

        assertEquals(2, itemsFromRepository.size());

        Item itemsFromRepository1 = itemsFromRepository.get(0);
        Item itemsFromRepository2 = itemsFromRepository.get(1);

        checkItem(item1, itemsFromRepository1);
        checkItem(item3, itemsFromRepository2);
    }

    @Test
    public void shouldGetOneItems() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(user2.getId(), pageable)
                .get()
                .collect(Collectors.toList());

        assertEquals(1, itemsFromRepository.size());

        Item itemsFromRepository1 = itemsFromRepository.get(0);

        checkItem(item2, itemsFromRepository1);
    }

    @Test
    public void shouldGetZeroItems() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(99L, pageable)
                .get()
                .collect(Collectors.toList());

        assertTrue(itemsFromRepository.isEmpty());
    }

    @Test
    public void shouldGetTwoAvailableItems() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.search("search1", pageable)
                .get()
                .collect(Collectors.toList());

        assertEquals(2, itemsFromRepository.size());

        Item itemsFromRepository1 = itemsFromRepository.get(0);
        Item itemsFromRepository2 = itemsFromRepository.get(1);

        checkItem(item1, itemsFromRepository1);
        checkItem(item2, itemsFromRepository2);
    }

    @Test
    public void shouldGetZeroItemsIfItemsNotAvailable() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.search("item3", pageable)
                .get()
                .collect(Collectors.toList());

        assertTrue(itemsFromRepository.isEmpty());
    }

    @Test
    public void shouldGetZeroItemsIfTextNotFound() {
        beforeEach();
        List<Item> itemsFromRepository = itemRepository.search("99", pageable)
                .get()
                .collect(Collectors.toList());

        assertTrue(itemsFromRepository.isEmpty());
    }
}