package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(Long itemId);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> findAllByNameOrDescription(String text);

}