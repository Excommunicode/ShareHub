package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    RequestMapper requestMapper;

    @Override
    public RequestDTO addRequest(RequestDTO requestDTO) {
        return requestMapper.toDTO(requestRepository.save(requestMapper.toModel(requestDTO)));
    }
}
