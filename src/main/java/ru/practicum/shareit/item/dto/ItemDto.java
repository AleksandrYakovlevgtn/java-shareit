package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;

    @NotBlank(groups = Create.class)
    @Size(max = 255)
    String name;

    @NotBlank(groups = Create.class)
    @Size(max = 1000)
    String description;

    @NotNull(groups = Create.class)
    Boolean available;
    Long ownerId;
    Long requestId;
}