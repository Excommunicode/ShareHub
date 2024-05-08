package ru.practicum.shareit.request;

import org.mapstruct.Mapper;

import java.util.List;

import static ru.practicum.shareit.utils.Constant.SPRING;

@Mapper(componentModel = SPRING)
public interface RequestMapper {

    RequestDTO toDTO(Request user);

    Request toModel(RequestDTO userDTO);

    List<RequestDTO> toDTOList(List<Request> modelList);

    List<Request> toRequestList(List<RequestDTO> userDTOList);
}
