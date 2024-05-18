package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "email"})
public class UserDTO {
    private Long id;
    private String name;
    private String email;
}