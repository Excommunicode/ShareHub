package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * The ItemService interface provides methods to manage items within the application.
 * It allows for adding, updating, retrieving single items or lists of items,
 * and searching items by name or description.
 */
@Service
public interface ItemService {

    /**
     * Adds a new item associated with a specific user.
     *
     * @param userId the ID of the user who owns the item
     * @param itemDTO the item to add, encapsulated in a {@link ItemDTO}
     * @return the added item, encapsulated in a {@link ItemDTO}, with its newly assigned ID
     */
    ItemDTO addItem(final Long userId, ItemDTO itemDTO);

    /**
     * Updates the details of an existing item.
     *
     * @param userId the ID of the user who owns the item
     * @param itemId the ID of the item to update
     * @param itemDTO the new details of the item, encapsulated in a {@link ItemDTO}
     * @return the updated item details, encapsulated in a {@link ItemDTO}
     */
    ItemDTO updateItem(final Long userId, Long itemId, ItemDTO itemDTO);

    /**
     * Retrieves a specific item by its ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return the requested item, encapsulated in a {@link ItemDTO}
     */
    ItemDTO getItem(final Long itemId);

    /**
     * Retrieves all items owned by a specific user.
     *
     * @param userId the ID of the user whose items to retrieve
     * @return a list of items owned by the user, each encapsulated in a {@link ItemDTO}
     */
    List<ItemDTO> getItems(final Long userId);

    /**
     * Searches for items by matching the provided text against their names and descriptions.
     *
     * @param text the text to match against item names and descriptions
     * @return a list of items that match the criteria, each encapsulated in a {@link ItemDTO}
     */
    List<ItemDTO> getItemsByNameOrDescription(final String text);
}
