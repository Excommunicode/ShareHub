package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDTO {
    Long id;
    @Future(message = "The start time musnt be in the past ")
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    BookingState status;
}
