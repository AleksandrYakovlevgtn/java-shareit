package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemExtendedDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemExtendedDto that = (ItemExtendedDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(available, that.available) && Objects.equals(ownerId, that.ownerId) && Objects.equals(requestId, that.requestId) && Objects.equals(lastBooking, that.lastBooking) && Objects.equals(nextBooking, that.nextBooking) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, ownerId, requestId, lastBooking, nextBooking, comments);
    }
}