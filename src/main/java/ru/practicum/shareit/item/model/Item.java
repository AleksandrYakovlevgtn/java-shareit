package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private Long id;
    private User owner;
    @NotNull
    @NotBlank(message = "Предмет не может быть без имени.")
    private String name;
    @NotBlank(message = "У предмета должно быть описание.")
    private String description;
    private Boolean available;
    private ItemRequest request;
}