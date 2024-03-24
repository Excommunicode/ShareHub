package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    ItemMapper mapper;
    UserService userService;
    UserMapper userMapper;

    @Transactional
    @Override
    public ItemDTO addItem(final Long userId, ItemDTO itemDTO) {
        isExistUserInDb(userId);
        if (!(itemDTO.getName() == null || itemDTO.getName().isEmpty() || itemDTO.getDescription() == null
                || itemDTO.getAvailable() == null)) {
            itemDTO.setOwner(userMapper.toModel(userService.getById(userId)));
            ItemDTO itemDTO1 = mapper.toDTO(repository.save(mapper.toModel(itemDTO)));
            return itemDTO1;
        }
        throw new ValidateException("не валидные данные", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @Override
    public ItemDTO updateItem(final Long userId, final Long itemId, ItemDTO itemDTO) {
        isExistUserInDb(userId);
        Item item = mapper.toModel(getItem(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("айдишники не совпадают");
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
        return mapper.toDTO(repository.findById(itemId).orElseThrow(() -> new NotFoundException("вещь не найдена")));
    }

    @Override
    public List<ItemDTO> getItems(final Long userId) {
        return mapper.toListDTO(repository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemDTO> getItemsByNameOrDescription(final String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        final String pattern = "%" + text + "%";
        return mapper.toListDTO(repository.findAllByNameOrDescription(pattern));

    }


    private void isExistUserInDb(final Long userId) {
        if (!userService.isExistUser(userId)) {
            throw new NotFoundException("такого пользователя нет");
        }
    }
}