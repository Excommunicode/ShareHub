package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
    ItemDTO addItem(Long userId, ItemDTO itemDTO);

    ItemDTO updateItem(Long userId, Long itemId, ItemDTO itemDTO);

    ItemDTO getItem(Long itemId);

    List<ItemDTO> getItems(Long userId);

    List<ItemDTO> getItemsByNameOrDescription(String text);
}
