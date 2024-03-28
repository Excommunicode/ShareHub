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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    @Pattern(regexp = REGEX_LOGIN, message = "This name is correct")
    String name;
    @NotNull(message = "field email should not be empty")
    @Email(regexp = REGEX_EMAIL, message = "Non standard writing of mail")
    String email;
}