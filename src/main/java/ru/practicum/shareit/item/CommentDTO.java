package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
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
public class CommentDTO {
    Long id;

    @NotNull(message = "Text cannot be null")
    @NotEmpty(message = "Text cannot be empty")
    String text;

    Long authorId;
    String authorName;
    LocalDateTime created;
}
