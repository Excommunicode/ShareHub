package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @DisplayName("Добавление пользователя в сервисе")
    @Test
    void addUserTest() {
        User user = new User();
        user.setName("FirstNameLastName");
        user.setEmail("email@example.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setName("FirstNameLastName");
        userDTO.setEmail("email@example.com");

        when(userMapper.toModel(userDTO)).thenReturn(user);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.addUser(userDTO);

        verify(userRepository, times(1)).saveAndFlush(user);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDTO.getName());
        assertThat(result.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @DisplayName("Обновление существующего пользователя")
    @Test
    void updateUser_existingUser_updatesUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);

        userService.updateUser(1L, userDTO);

        verify(userMapper, times(1)).toDTO(any());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("Обновление не существующего пользователя с ошибкой")
    @Test
    void updateUser_notExistingUser_returnsNotFoundException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            userService.updateUser(1L, userDTO);
        } catch (NotFoundException e) {
            assertThat(e).isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("Удаление существующего пользователя")
    @Test
    void deleteUser_existingUser_deletesUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(any());
    }

    @DisplayName("Удаление не существующего пользователя с ошибкой")
    @Test
    void deleteUser_notExistingUser_returnsNotFoundException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        try {
            userService.deleteUser(100L);
        } catch (NotFoundException e) {
            assertThat(e).isInstanceOf(NotFoundException.class);
        }
    }


    @Test
    void getUser_existingUser_returnsUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDTO result = userService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(userDTO.getName());
        assertThat(result.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    void getUser_notExistingUser_returnsNotFoundException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        try {
            userService.getById(1L);
        } catch (NotFoundException e) {
            assertThat(e).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    void getAllUsers_existingUser_returnsUsers() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<User> userList = List.of(user);
        when(userRepository.findAll()).thenReturn(userList);
    }

    @Test
    void getAllUsers_notExistingUser_returnsNotFoundException() {
        assertEquals(Collections.emptyList(), userService.getAll(0, 20));
    }


    @Test
    void isExistUserTest_existingUser_returnsTrue() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("some@gmail.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(any())).thenReturn(userDTO);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        boolean result = userService.isExistUser(1L);
        assertTrue(result);
    }

    @Test
    void getAllUsers_whenNoUsers_mustReturnEmptyList() {
        int page = 0;
        int size = 20;

        when(userRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        List<UserDTO> result = userService.getAll(page, size);

        assertTrue(result.isEmpty());
    }

}
