package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDTO addUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO user);

    UserDTO getById(Long id);

    void deleteUser(Long id);

    List<UserDTO> getAll();

    boolean isExistUser(Long id);
}