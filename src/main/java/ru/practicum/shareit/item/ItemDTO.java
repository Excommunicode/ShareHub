package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingShortDTO;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDTO {

    Long id;

    String name;

    String description;

    Boolean available;

    User owner;

    Request request;

    BookingShortDTO lastBooking;

    BookingShortDTO nextBooking;

    List<CommentDTO> comments;

    LocalDateTime created = LocalDateTime.now();

    @Override
    public String toString() {
        return "ItemDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", owner=" + owner +
                ", request=" + request +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                ", comments=" + comments +
                ", created=" + created +
                '}';
    }
}
