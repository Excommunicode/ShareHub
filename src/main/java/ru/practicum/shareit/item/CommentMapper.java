package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mappings({@Mapping(source = "author.id", target = "authorId"),
            @Mapping(source = "author.name", target = "authorName")})
    CommentDTO toDTO(Comment comment);

    @Mappings({@Mapping(source = "authorId", target = "author.id"),
            @Mapping(source = "authorName", target = "author.name")})
    Comment toModel(CommentDTO commentDTO);

    List<CommentDTO> toDTOList(List<Comment> comments);

    List<Comment> toModelList(List<CommentDTO> commentDTOs);
}