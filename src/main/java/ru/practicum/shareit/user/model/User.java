package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @NonNull
    private String name;
    @NotBlank(message = "Email должен быть заполнен.")
    @Email(message = "Ошибку в email.")
    private String email;
}