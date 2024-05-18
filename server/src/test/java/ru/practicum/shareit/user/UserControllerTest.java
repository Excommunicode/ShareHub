package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.user.UserConstant.REGEX_EMAIL;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    @DisplayName("Создание пользователя: при корректном вводе должен возвращаться статус 201 Created")
    void createUser_WhenValidInput_ShouldReturn201Created() {

        UserDTO user = UserDTO.builder()
                .name("John")
                .email("john@example.com")
                .build();


        Pattern compile = Pattern.compile(REGEX_EMAIL);
        assert compile.matcher(user.getEmail()).matches();
        assert user.getName() != null && !user.getName().isEmpty();

        when(userService.addUser(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }


    @Test
    @SneakyThrows
    @DisplayName("Получение пользователя по ID: при корректном вводе должен возвращаться пользователь")
    void getUserById_WhenValidInput_ShouldReturnUser() {
        UserDTO user = UserDTO.builder()
                .name("John")
                .email("john@example.com")
                .build();

        when(userService.getById(any(Long.class))).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    @SneakyThrows
    @DisplayName("Удаление пользователя: при корректном вводе должен возвращаться статус No Content")
    void deleteUser_WhenValidInput_ShouldReturnNoContent() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    @DisplayName("Обновление пользователя: при корректном вводе должен возвращаться обновленный пользователь")
    void updateUser_WhenValidInput_ShouldReturnUpdatedUser() {
        UserDTO userAfterUpdate = UserDTO.builder()
                .name("JohnSmith")
                .email("johnsmith@example.com")
                .build();

        when(userService.updateUser(any(Long.class), any(UserDTO.class))).thenReturn(userAfterUpdate);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAfterUpdate)))
                .andExpect(status().isOk());
    }
}