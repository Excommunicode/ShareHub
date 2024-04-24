package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingShortDTO;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.utils.Marker.OnCreate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDTO {

    Long id;

    @NotBlank(message = "Name cannot be blank", groups = OnCreate.class)
    String name;

    @NotBlank(message = "Description cannot be blank", groups = OnCreate.class)
    String description;

    @NotNull(message = "Available cannot be null", groups = OnCreate.class)
    Boolean available;

    User owner;

    BookingShortDTO lastBooking;

    BookingShortDTO nextBooking;

    List<CommentDTO> comments;

    LocalDateTime created = LocalDateTime.now();

    Long requestId;
}