package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundExeception;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final ItemMapper mapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public ItemDTO addItem(final Long userId, ItemDTO itemDTO) {
        isExistUserInDb(userId);
        if (!(itemDTO.getName() == null || itemDTO.getName().isEmpty() || itemDTO.getDescription() == null
                || itemDTO.getAvailable() == null)) {
            itemDTO.setOwner(userMapper.toModel(userService.getById(userId)));
            ItemDTO itemDTO1 = mapper.toDTO(repository.save(mapper.toModel(itemDTO)));
            return itemDTO1;
        } else {
            throw new ValidateException("не валидные данные", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public ItemDTO updateItem(final Long userId, final Long itemId, ItemDTO itemDTO) {
        isExistUserInDb(userId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundExeception("пользователя не удалось обновить"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundExeception("айдишники не совпадают");
        }
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        repository.save(item);
        return mapper.toDTO(item);
    }

    @Override
    public ItemDTO getItem(final Long itemId) {
        return mapper.toDTO(repository.findById(itemId).orElseThrow(() -> new NotFoundExeception("вещь не найдена")));
    }

    @Override
    public List<ItemDTO> getItems(Long userId) {
        return mapper.toListDTO(repository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemDTO> getItemsByNameOrDescription(String text) {
        if (text.isEmpty() || text.equals(null)) {
            return new ArrayList<>();
        }
        final String pattern = "%" + text + "%";
        return mapper.toListDTO(repository.findAllByNameOrDescription(pattern));

    }


    private void isExistUserInDb(final Long userId) {
        if (!userService.isExistUser(userId)) {
            throw new NotFoundExeception("такого пользователя нет");
        }
    }
}