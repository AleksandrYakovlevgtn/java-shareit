package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    @NotBlank
    @Size(max = 255)
    String name;

    @NotBlank
    @Size(max = 1024)
    String description;

    @NotNull
    Boolean available;

    Long requestId;
}