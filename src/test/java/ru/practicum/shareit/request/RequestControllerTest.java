package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemConstant.X_SHARER_USER_ID;

@AutoConfigureMockMvc
@WebMvcTest(controllers = RequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;


    private User user;
    private RequestDTOResponse request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user")
                .email("user@email.com")
                .build();
        request = RequestDTOResponse.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.now())
                .build();
    }


    @Test
    @SneakyThrows
    @DisplayName("Создание запроса: при корректном вводе должен возвращаться статус 201 Created")
    void createRequest() {
        Long userId = 1L;
        when(requestService.addRequestDTO(any(RequestDTO.class), any(Long.class))).thenReturn(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isCreated());

    }

    @Test
    @SneakyThrows
    void getRequests() {
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
    }

    @Test
    @SneakyThrows
    void getRequest() {
    }
}