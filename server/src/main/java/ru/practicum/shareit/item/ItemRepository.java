package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Finds all items by the owner's ID and returns them in ascending order by ID.
     *
     * @param id the ID of the owner
     * @return a list of items owned by the owner, ordered by ID
     */
    List<Item> findAllByOwnerIdOrderById(Long id, Pageable pageable);


    /**
     * Finds items by name or description and availability status.
     *
     * @param name        the name to search for (case-insensitive)
     * @param description the description to search for (case-insensitive)
     * @param offset      the offset for paginated results
     * @param limit       the maximum number of items to return
     * @return a list of items matching the name or description and availability status, limited by the offset and limit parameters
     */
    @Query(nativeQuery = true,
            value = "SELECT i.* " +
                    "FROM items i " +
                    "WHERE LOWER(i.name) LIKE :name " +
                    "OR LOWER(i.description) LIKE :description " +
                    "AND i.is_available = true " +
                    "LIMIT :limit " +
                    "OFFSET :offset")
    List<Item> findByNameOrDescriptionAndAvailable(String name, String description, Integer offset, Integer limit);

    /**
     * Retrieves all items by request ID.
     *
     * @param id The ID of the request.
     * @return A list of items matching the given request ID.
     */
    List<Item> findAllByRequestId(Long id);

    /**
     * Finds all items owned by a specific user and with a specific request ID.
     *
     * @param requestId The ID of the request.
     * @return A list of items matching the owner ID and request ID.
     */
    List<Item> findAllByOwner_IdAndRequestId(Long requestId, Long id);
}