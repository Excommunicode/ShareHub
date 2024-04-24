package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = REPEATABLE_READ)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    RequestMapper requestMapper;
    RequestMapperResponse requestMapperResponse;
    UserRepository userRepository;
    UserMapper userMapper;
    ItemRepository itemRepository;
    ItemMapper itemMapper;

    @Transactional
    @Override
    public RequestDTOResponse addRequestDTO(RequestDTO requestDTO, final Long userId) {
        log.info("Attempting to add request for user with id: {}", userId);
        UserDTO userDTO = findUserDTO(userId);

        log.debug("User found: {}", userDTO);

        requestDTO.setRequestor(userMapper.toModel(userDTO));
        RequestDTO savedRequestDTO = requestMapper.toDTO(requestRepository.save(requestMapper.toModel(requestDTO)));
        log.info("Request saved for user id: {}", userId);

        RequestDTOResponse responseDTO = requestMapperResponse.toDTO(requestMapper.toModel(savedRequestDTO));
        responseDTO.setCreated(LocalDateTime.now());
        log.debug("RequestDTOResponse created with created date set to now: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public List<RequestDTOResponse> getRequestsDTO(final Long userId) {
        UserDTO userDTO = findUserDTO(userId);

        List<RequestDTOResponse> dtoList = requestMapperResponse.toDTOList(requestRepository.findAllByRequestor_Id(userId));
        List<RequestDTOResponse> collect = dtoList.stream()
                .peek(dto -> dto.setCreated(LocalDateTime.now()))
                .collect(Collectors.toList());

        return collect;
    }


    private UserDTO findUserDTO(final Long userId) {
        return userMapper.toDTO(userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id: {} not found", userId);
            return new NotFoundException("User not found with id: " + userId);
        }));
    }
}
