package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long id);

    List<Item> findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);


    @Query(nativeQuery = true,
            value = "SELECT i.* " +
                    "FROM Items i " +
                    "LEFT JOIN bookings b on i.id = b.item_id " +
                    "WHERE i.owner_id = ?1 " +
                    "AND b.item_id = ?2 " +
                    "AND end_date < current_timestamp() ")
    List<Item> findAllByOwner_Id(Long ownerId);
}