
package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import ru.practicum.shareit.user.User;

class CommentMapperTest {

    @InjectMocks
    private static final CommentMapper COMMENT_MAPPER = Mappers.getMapper(CommentMapper.class);

    CommentDTO commentDTO;
    Comment comment;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setId(1L);
        author.setName("TestUser");

        comment = new Comment();
        comment.setText("First Comment");
        comment.setAuthor(author);

        commentDTO = new CommentDTO();
        commentDTO.setText("First Comment");
    }

    @Test
    void testToDto() {
        CommentDTO resultDTO = COMMENT_MAPPER.toDTO(comment);
        assertEquals(commentDTO.getText(), resultDTO.getText());
    }
}
