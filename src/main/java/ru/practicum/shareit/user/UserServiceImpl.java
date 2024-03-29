package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictValidException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper mapper;


    @Override
    public UserDTO addUser(UserDTO userDTO) {
        log.info("Adding new user with email: {}", userDTO.getEmail());

        checkEmailInDB(userDTO);
        final UserDTO newUserDTO = mapper.toDTO(userRepository.addUser(mapper.toModel(userDTO)));

        log.info("User with email: {} added successfully with ID: {}", newUserDTO.getEmail(), newUserDTO.getId());
        return newUserDTO;
    }

    @Override
    public UserDTO updateUser(final Long id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);
        userDTO.setId(id);
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("User not found for ID: " + id));

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getEmail() != null) {
            checkUserIdByEmail(userDTO);
            userRepository.deleteEmail(user.getEmail());
            user.setEmail(userDTO.getEmail());
        }

        userRepository.updateUser(user);
        log.info("User with ID: {} updated successfully", user.getId());
        return mapper.toDTO(user);
    }

    @Override
    public UserDTO getById(final Long id) throws NotFoundException {
        log.info("Retrieving user by ID: {}", id);
        final UserDTO userDTO = mapper.toDTO(userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found for ID: " + id)));
        log.info("User retrieved with ID: {}", id);
        return userDTO;
    }

    @Transactional
    @Override
    public void deleteUser(final Long id) throws NotFoundException {
        log.info("Deleting user with ID: {}", id);

        userRepository.deleteUser(id);

        log.info("User with ID: {} deleted successfully", id);
    }

    @Override
    public List<UserDTO> getAll() {
        log.info("Retrieving all users");

        final List<UserDTO> users = mapper.toDTOList(userRepository.getAll());

        log.info("Total users retrieved: {}", users.size());
        return users;
    }

    @Override
    public boolean isExistUser(final Long id) {
        final boolean exists = userRepository.existsById(id);

        log.info("Checking existence of user with ID: {}. Exists: {}", id, exists);
        return exists;
    }

    private void checkEmailInDB(final UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail()) != null) {
            log.error("Email already exists in database: {}", userDTO.getEmail());
            throw new ConflictValidException("у пользователя есть почта и эмейл");
        }
    }

    private void checkUserIdByEmail(final UserDTO userDTO) {
        final Long userIdByEmail = userRepository.existsByEmail(userDTO.getEmail());
        if (userIdByEmail != null) {
            if (!userIdByEmail.equals(userDTO.getId())) {
                log.error("Email already in use by another user: {}", userDTO.getEmail());
                throw new ConflictValidException("This user email has been busy");
            }
        }
    }
}
