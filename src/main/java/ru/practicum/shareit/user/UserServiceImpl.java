package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundExeception;
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
        if (!repository.existsByEmail(userDTO.getEmail())) {
            UserDTO userDTO1 = mapper.toDTO(repository.saveAndFlush(mapper.toModel(userDTO)));
            return mapper.toDTO(repository.findById(userDTO1.getId()).orElse(null));
        }
        throw new ValidateException("у пользователя есть почта", HttpStatus.CONFLICT);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundExeception("The user was not found"));
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
    public UserDTO getById(Long id) throws NotFoundExeception {
        return mapper.toDTO(repository.getById(id));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) throws NotFoundExeception {
        repository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAll() {
        return mapper.toDTOList(repository.findAll());
    }

    @Override
    public boolean isExistUser(Long id) {
        return repository.existsById(id);
    }
}
