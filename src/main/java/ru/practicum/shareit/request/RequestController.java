package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static ru.practicum.shareit.utils.Constant.X_SHARER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {
    RequestService requestService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDTOResponse createRequest(@Valid @RequestBody final RequestDTO requestDTO,
                                            @RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return requestService.addRequestDTO(requestDTO, userId);
    }

    @GetMapping
    public List<RequestDTOResponse> getRequests(@RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return requestService.getRequestsDTO(userId);
    }

    @GetMapping("/all")
    public List<RequestDTOResponse> getAllRequests(@RequestHeader(X_SHARER_USER_ID) final Long userId,
                                                   @RequestParam final Integer from,
                                                   @RequestParam final Integer size) {
        return null;
    }
}
