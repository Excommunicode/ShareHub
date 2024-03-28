package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository repository;
    UserMapper mapper;

    @Transactional
    @Override
    public UserDTO addUser(UserDTO userDTO) {
        isExistUserByEmail(userDTO);
        UserDTO userDTO1 = mapper.toDTO(repository.save(mapper.toModel(userDTO)));
        return mapper.toDTO(repository.findById(userDTO1.getId()).orElse(null));
    }

    @Transactional
    @Override
    public UserDTO updateUser(final Long id, UserDTO userDTO) {
        User user = mapper.toModel(getById(id));
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        repository.save(user);
        return mapper.toDTO(user);
    }

    @Override
    public UserDTO getById(final Long id) throws NotFoundException {
        return mapper.toDTO(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не был найден что делать?")));
    }

    @Transactional
    @Override
    public void deleteUser(final Long id) throws NotFoundException {
        repository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAll() {
        return mapper.toDTOList(repository.findAll());
    }

    @Override
    public boolean isExistUser(final Long id) {
        return repository.existsById(id);
    }

    private void isExistUserByEmail(UserDTO userDTO) {
        if (repository.existsByEmail(userDTO.getEmail())) {
            throw new ValidateException("у пользователя есть почта и эмейл", HttpStatus.CONFLICT);
        }
    }
}
