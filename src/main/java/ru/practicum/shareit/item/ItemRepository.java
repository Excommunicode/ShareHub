package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemConstant.ITEM_REPOSITORY;

@Repository(value = ITEM_REPOSITORY)
public interface ItemRepository {
    Item addItem(Item item);

    Optional<Item> updateItem(Item item);

    Optional<Item> getItem(Long itemId);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> findAllByNameOrDescription(String text);

}