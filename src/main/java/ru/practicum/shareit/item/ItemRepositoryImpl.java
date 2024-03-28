package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, Item> ITEMS = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item addItem(Item item) {
        item.setId(++id);
        ITEMS.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return ITEMS.put(item.getId(), item);
    }

    @Override
    public Optional<Item> getItem(final Long itemId) {
        return Optional.of(ITEMS.get(itemId));
    }

    @Override
    public List<Item> findAllByOwnerId(final Long userId) {
        return ITEMS.values().stream()
                .filter(x -> x.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByNameOrDescription(final String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : ITEMS.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
