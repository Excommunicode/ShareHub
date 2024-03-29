package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    ItemMapper mapper;
    UserService userService;
    UserMapper userMapper;

    @Override
    public ItemDTO addItem(final Long userId, ItemDTO itemDTO) {
        log.info("Attempting to add item for user ID: {}", userId);

        isExistUserInDb(userId);
        if (itemDTO.getName() == null || itemDTO.getName().isEmpty() || itemDTO.getDescription() == null ||
                itemDTO.getAvailable() == null) {
            log.error("Invalid data for adding item: {}, by user ID: {}", itemDTO, userId);
            throw new BadRequestException("Invalid data for item");
        }
        itemDTO.setOwner(userMapper.toModel(userService.getById(userId)));
        ItemDTO newItemDTO = mapper.toDTO(itemRepository.addItem(mapper.toModel(itemDTO)));

        log.info("Item successfully added with ID: {}", newItemDTO.getId());
        return newItemDTO;
    }

    @Override
    public ItemDTO updateItem(final Long userId, final Long itemId, ItemDTO itemDTO) {
        log.info("Updating item with ID: {} for user ID: {}", itemId, userId);

        isExistUserInDb(userId);
        Item item = mapper.toModel(getItem(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            log.error("User ID: {} does not match owner ID for item ID: {}", userId, itemId);
            throw new NotFoundException("User ID does not match owner ID");
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

        itemRepository.updateItem(item);

        log.info("Item with ID: {} updated successfully", item.getId());
        return mapper.toDTO(item);
    }

    @Override
    public ItemDTO getItem(final Long itemId) {
        log.info("Retrieving item by ID: {}", itemId);

        final ItemDTO itemDTO = mapper.toDTO(itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found")));

        log.info("Item retrieved with ID: {}", itemId);
        return itemDTO;
    }

    @Override
    public List<ItemDTO> getItems(final Long userId) {
        log.info("Retrieving items for user ID: {}", userId);

        final List<ItemDTO> items = mapper.toListDTO(itemRepository.findAllByOwnerId(userId));

        log.info("Total items retrieved for user ID: {}: {}", userId, items.size());
        return items;
    }

    @Override
    public List<ItemDTO> getItemsByNameOrDescription(final String text) {
        log.info("Searching for items by name or description matching: {}", text);

        if (text.isEmpty()) {
            log.info("Search text was empty, returning empty list.");
            return new ArrayList<>();
        }
        final List<ItemDTO> items = mapper.toListDTO(itemRepository.findAllByNameOrDescription(text));

        log.info("Total items found matching {}: {}", text, items.size());
        return items;
    }

    private void isExistUserInDb(final Long userId) {
        log.info("Checking existence of user with ID: {}", userId);

        if (!userService.isExistUser(userId)) {
            log.error("No user found with ID: {}", userId);
            throw new NotFoundException("User does not exist");
        }
    }
}
