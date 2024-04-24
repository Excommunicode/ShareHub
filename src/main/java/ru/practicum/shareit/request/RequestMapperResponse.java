package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapperResponse {

    RequestDTOResponse toDTO(Request user);

    Request toModel(RequestDTOResponse userDTO);

    List<RequestDTOResponse> toDTOList(List<Request> modelList);

    List<Request> toRequestList(List<RequestDTOResponse> userDTOList);
}
