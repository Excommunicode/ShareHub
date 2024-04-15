package ru.practicum.shareit.user;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @InheritInverseConfiguration
    UserDTO toDTO(User user);

    User toModel(UserDTO userDTO);

    @IterableMapping(elementTargetType = UserDTO.class)
    List<UserDTO> toDTOList(List<User> modelList);

    List<User> toUserList(List<UserDTO> userDTOList);
}