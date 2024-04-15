package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {
    Long id;
    String text;
    Long authorId;
    String authorName;
    LocalDateTime created;
}
