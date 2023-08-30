package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @NotBlank(groups = Create.class)
    @Size(max = 255, groups = {Create.class, Update.class})
    String name;
    @NotBlank(groups = Create.class)
    @Size(max = 1024, groups = {Create.class, Update.class})
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
    Long requestId;
}