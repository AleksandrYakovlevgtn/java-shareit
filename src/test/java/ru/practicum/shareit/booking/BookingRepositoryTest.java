package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    private final int from = Integer.parseInt(Constants.PAGE_DEFAULT_FROM);
    private final int size = Integer.parseInt(Constants.PAGE_DEFAULT_SIZE);
    private final Pageable pageable = PageRequest.of(from / size, size);
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 8, 12, 10, 0, 0);
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
    private final Booking bookingPast = Booking.builder()
            .start(dateTime.minusYears(10))
            .end(dateTime.minusYears(9))
            .item(item1)
            .booker(user2)
            .status(Status.APPROVED)
            .build();
    private final Booking bookingCurrent = Booking.builder()
            .start(dateTime.minusYears(5))
            .end(dateTime.plusYears(5))
            .item(item1)
            .booker(user2)
            .status(Status.APPROVED)
            .build();
    private final Booking bookingFuture = Booking.builder()
            .start(dateTime.plusYears(8))
            .end(dateTime.plusYears(9))
            .item(item1)
            .booker(user2)
            .status(Status.WAITING)
            .build();
    private final Booking bookingRejected = Booking.builder()
            .start(dateTime.plusYears(9))
            .end(dateTime.plusYears(10))
            .item(item1)
            .booker(user2)
            .status(Status.REJECTED)
            .build();

    public void beforeEach() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(bookingPast);
        entityManager.persist(bookingCurrent);
        entityManager.persist(bookingFuture);
        entityManager.persist(bookingRejected);
    }

    @Test
    public void shouldGetAll() {
        beforeEach();
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(user2.getId(), pageable)
                .get().collect(Collectors.toList());

        assertEquals(4, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
        assertEquals(bookingCurrent.getId(), result.get(2).getId());
        assertEquals(bookingPast.getId(), result.get(3).getId());
    }

    @Test
    public void shouldGetEmpty() {
        beforeEach();
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(user1.getId(), pageable)
                .get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetCurrent() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user2.getId(), dateTime,
                        dateTime, pageable
                ).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingCurrent.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty2() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user1.getId(), dateTime,
                        dateTime, pageable
                ).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetPast() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(user2.getId(), dateTime,
                        Status.APPROVED, pageable
                ).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingPast.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty3() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(user1.getId(), dateTime,
                        Status.APPROVED, pageable
                ).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetFuture() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStartAfterOrderByStartDesc(user2.getId(), dateTime, pageable)
                .get().collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
    }

    @Test
    public void shouldGetEmpty4() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStartAfterOrderByStartDesc(user1.getId(), dateTime, pageable)
                .get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetWaiting() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStatusEqualsOrderByStartDesc(user2.getId(), Status.WAITING, pageable)
                .get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingFuture.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetRejected() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStatusEqualsOrderByStartDesc(user2.getId(), Status.REJECTED, pageable)
                .get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty5() {
        beforeEach();
        List<Booking> result = bookingRepository
                .findByBookerIdAndStatusEqualsOrderByStartDesc(user1.getId(), Status.WAITING, pageable)
                .get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetAll2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(user1.getId(), pageable)
                .get().collect(Collectors.toList());

        assertEquals(4, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
        assertEquals(bookingCurrent.getId(), result.get(2).getId());
        assertEquals(bookingPast.getId(), result.get(3).getId());
    }

    @Test
    public void shouldGetEmpty6() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(user2.getId(), pageable)
                .get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetCurrent2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user1.getId(), dateTime, dateTime, pageable).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingCurrent.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty8() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user2.getId(), dateTime, dateTime, pageable).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetPast2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                user1.getId(), dateTime, Status.APPROVED, pageable).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingPast.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty9() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                user2.getId(), dateTime, Status.APPROVED, pageable).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetFuture2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(user1.getId(),
                dateTime, pageable
        ).get().collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
    }

    @Test
    public void shouldGetEmpty10() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(user2.getId(),
                dateTime, pageable
        ).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetWaiting2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                user1.getId(),
                Status.WAITING,
                pageable
        ).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingFuture.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetRejected2() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                user1.getId(),
                Status.REJECTED,
                pageable
        ).get().collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty11() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                user2.getId(),
                Status.WAITING,
                pageable
        ).get().collect(Collectors.toList());

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetLastBookings() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                item1.getId(), dateTime, Status.APPROVED);

        assertEquals(2, result.size());
        assertEquals(bookingCurrent.getId(), result.get(0).getId());
        assertEquals(bookingPast.getId(), result.get(1).getId());
    }

    @Test
    public void shouldGetEmpty12() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                item1.getId(), dateTime.minusYears(15), Status.APPROVED);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetNextBookings() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                item1.getId(), dateTime, Status.WAITING);

        assertEquals(1, result.size());
        assertEquals(bookingFuture.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty13() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                item1.getId(), dateTime, Status.APPROVED);

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldGetFinishedBookings() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(
                item1.getId(), user2.getId(), dateTime, Status.APPROVED);

        assertEquals(1, result.size());
        assertEquals(bookingPast.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetEmpty14() {
        beforeEach();
        List<Booking> result = bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(
                item1.getId(), user2.getId(), dateTime.minusYears(15), Status.APPROVED);

        assertTrue(result.isEmpty());
    }
}