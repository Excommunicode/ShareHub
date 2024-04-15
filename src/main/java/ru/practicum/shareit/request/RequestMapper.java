package ru.practicum.shareit.request;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDTO toDTO(Request itemRequest);

    Request toModel(RequestDTO itemRequestDTO);

    List<RequestDTO> toListDTO(List<Request> itemRequestList);

    List<Request> toListModel(List<RequestDTO> itemRequestDTOS);
}
