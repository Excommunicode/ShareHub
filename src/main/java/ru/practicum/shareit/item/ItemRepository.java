package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ru.practicum.shareit.item.ItemConstant.ITEM_REPOSITORY;

@Repository(value = ITEM_REPOSITORY)
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(final Long id);

    @Query(nativeQuery = true,
            value = "SELECT i.* " +
                    "FROM ITEMS i " +
                    "WHERE i.IS_AVAILABLE = true " +
                    "AND (LOWER(i.name) LIKE LOWER(?1) " +
                    "OR LOWER(i.description) LIKE LOWER(?1))")
    List<Item> findAllByNameOrDescription(String text1);

}