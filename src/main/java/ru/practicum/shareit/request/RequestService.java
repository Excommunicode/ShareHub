package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {

    RequestDTOResponse addRequestDTO(RequestDTO request, Long userId);

    List<RequestDTOResponse> getRequestsDTO(Long userId);
}
