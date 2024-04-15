package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long id);

    List<Item> findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);

    boolean existsItemByIdAndOwner_Id(Long item_id, Long owner_id);
}