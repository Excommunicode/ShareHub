package ru.practicum.shareit.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDTO {
    Long id;
    String description;
    User requestor_id;
}