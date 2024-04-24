package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = REPEATABLE_READ)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository repository;
    UserMapper mapper;

    @Transactional
    @Override
    public UserDTO addUser(UserDTO userDTO) {
        log.debug("Adding a user with email: {}", userDTO.getEmail());

        UserDTO savedUserDTO = mapper.toDTO(repository.saveAndFlush(mapper.toModel(userDTO)));

        log.info("User added with ID: {}", savedUserDTO.getId());
        return savedUserDTO;
    }

    @Transactional
    @Override
    public UserDTO updateUser(final Long id, UserDTO userDTO) {
        log.debug("Updating user with ID: {}", id);

        User existingUser = findUserById(id);
        updateUserInfoFromDTO(existingUser, userDTO);
        repository.save(existingUser);

        log.info("User updated with ID: {}", id);
        return mapper.toDTO(existingUser);
    }

    @Transactional
    @Override
    public void deleteUser(final Long id) {
        log.debug("Deleting user with ID: {}", id);

        repository.deleteById(id);

        log.info("User deleted with ID: {}", id);
    }

    @Override
    public UserDTO getById(final Long id) {
        log.debug("Retrieving user with ID: {}", id);

        User user = findUserById(id);

        log.info("User retrieved with ID: {}", id);
        return mapper.toDTO(user);
    }


    @Override
    public List<UserDTO> getAll() {
        log.debug("Retrieving all users");

        final List<UserDTO> userDTOs = new ArrayList<>();
        final Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, 10, sort);
        Page<User> userPage = repository.findAll(page);
        do {
            userDTOs.addAll(mapper.toDTOList(userPage.getContent()));
            if (userPage.hasNext()) {
                page = userPage.nextOrLastPageable();
                userPage = repository.findAll(page);
            } else {
                page = null;
            }
        } while (page != null);

        log.info("Total users retrieved: {}", userDTOs.size());
        return userDTOs;
    }

    @Override
    public boolean isExistUser(final Long id) {
        final boolean exists = repository.existsById(id);

        log.debug("User existence check for ID: {} - Exists: {}", id, exists);
        return exists;
    }

    private User findUserById(final Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new NotFoundException("The user was not found");
        });
    }

    private void updateUserInfoFromDTO(final User user, final UserDTO userDTO) {
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
    }
}