package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {
    RequestService requestService;

    @PostMapping
    public RequestDTO saveRequest(@Valid @RequestBody RequestDTO requestDTO) {
        return requestService.addRequest(requestDTO);
    }
}