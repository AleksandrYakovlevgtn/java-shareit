package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.constants.Create;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @Size(max = 255)
    String name;
    @NotBlank(groups = {Create.class})
    @Email
    String email;
}