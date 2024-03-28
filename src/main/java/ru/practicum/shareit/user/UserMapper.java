package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    @InheritInverseConfiguration

    UserDTO toDTO(User user);

    User toModel(UserDTO userDTO);
//@IterableMapping(elementTargetType = CarDto.class)
    List<UserDTO> toDTOList(List<User> modelList);

    List<User> toUserList(List<UserDTO> userDTOList);
}