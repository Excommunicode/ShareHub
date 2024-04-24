package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static ru.practicum.shareit.utils.Constant.REGEX_EMAIL;
import static ru.practicum.shareit.utils.Constant.REGEX_LOGIN;
import static ru.practicum.shareit.utils.Marker.OnCreate;
import static ru.practicum.shareit.utils.Marker.OnUpdate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    Long id;

    @NotBlank(message = "Name cannot be blank", groups = OnCreate.class)
    @Pattern(regexp = REGEX_LOGIN, message = "This name is incorrect", groups = {OnCreate.class, OnUpdate.class})
    String name;

    @NotNull(message = "Email cannot be null", groups = OnCreate.class)
    @Email(regexp = REGEX_EMAIL, message = "Non standard writing of mail", groups = {OnCreate.class, OnUpdate.class})
    String email;
}