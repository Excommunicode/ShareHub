package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDTOResponse {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDTO item;
    UserDTO booker;
    BookingState status;
}
