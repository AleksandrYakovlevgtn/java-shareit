package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @Size(max = 255, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    String email;
}