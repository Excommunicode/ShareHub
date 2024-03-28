package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;


    @Transactional
    @Override
    public UserDTO addUser(UserDTO userDTO) {

        checkEmailInDB(userDTO);

        final UserDTO userDTO1 = mapper.toDTO(userRepository.addUser(mapper.toModel(userDTO)));

        return userDTO1;
    }

    @Transactional
    @Override
    public UserDTO updateUser(final Long id, UserDTO userDTO) {

        userDTO.setId(id);
        User user = mapper.toModel(getById(userDTO.getId()));
        if (user == null) {
            throw new NotFoundException("This user is not be in database");
        }

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getEmail() != null) {
            checkUserIdByEmail(userDTO);
            userRepository.deleteEmail(user.getEmail());
            user.setEmail(userDTO.getEmail());
        }

        userRepository.updateUser(user.getId(), user);
        return mapper.toDTO(user);
    }

    @Override
    public UserDTO getById(final Long id) throws NotFoundException {
        return mapper.toDTO(userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не был найден что делать?")));
    }

    @Transactional
    @Override
    public void deleteUser(final Long id) throws NotFoundException {
        userRepository.deleteUser(id);
    }

    @Override
    public List<UserDTO> getAll() {
        return mapper.toDTOList(userRepository.getAll());
    }

    @Override
    public boolean isExistUser(final Long id) {
        return userRepository.existsById(id);
    }

    private void checkEmailInDB(final UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail()) != null) {
            throw new ValidateException("у пользователя есть почта и эмейл", HttpStatus.CONFLICT);
        }
    }

    private void checkUserIdByEmail(final UserDTO userDTO) {
        final Long userIdByEmail = userRepository.existsByEmail(userDTO.getEmail());
        if (userIdByEmail != null) {
            if (!userIdByEmail.equals(userDTO.getId())) {
                throw new ValidateException("This user email has been busy", HttpStatus.CONFLICT);
            }
        }
    }
}
