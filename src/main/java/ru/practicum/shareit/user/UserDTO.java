package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static ru.practicum.shareit.user.UserConstant.REGEX_EMAIL;
import static ru.practicum.shareit.user.UserConstant.REGEX_LOGIN;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    @Pattern(regexp = REGEX_LOGIN, message = "bebra")
    String name;
    @NotNull
    @Email(regexp = REGEX_EMAIL, message = "Non standard writing of mail")
    String email;
}
