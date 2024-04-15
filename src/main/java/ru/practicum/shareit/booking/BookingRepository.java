package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Booking} entities.
 * This interface extends JpaRepository, providing CRUD operations and custom queries
 * for handling booking data in the database.
 * It includes methods to find, create, update, and delete bookings, as well as
 * to perform more complex queries based on booker ID, item ID, booking status, and time constraints.
 * These operations facilitate the management of booking data across the application,
 * supporting various business requirements related to scheduling and reservation management.
 *
 * @author Farukh
 * @version 1.0
 * @since 1.0
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Checks if a booking exists for a given booker, item, status, and end time.
     *
     * @param bookerId  the ID of the booker
     * @param itemId    the ID of the item
     * @param status    the statuses to consider in the search
     * @param endBefore the end time to compare against
     * @return true if at least one booking exists, false otherwise
     */
    boolean existsByBookerIdAndItemIdAndStatusInAndEndBefore(Long bookerId, Long itemId, List<BookingState> status, LocalDateTime endBefore);

    /**
     * Retrieves a specific booking by its ID and booker's ID or by its ID and item owner's ID.
     *
     * @param id        the booking ID
     * @param booker_id the booker's ID
     * @param id2       the second ID for the booking
     * @param item_id   the item owner's ID
     * @return the found booking, or null if none found
     */
    Booking getBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(Long id, Long booker_id, Long id2, Long item_id);

    /**
     * Finds bookings for a booker that started before and end after a specified time.
     *
     * @param booker_id the ID of the booker
     * @param start     the start time to compare
     * @param end       the end time to compare
     * @return a list of bookings
     */
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long booker_id, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves a list of bookings for a booker, ordered by booking ID in descending order.
     *
     * @param booker_id the ID of the booker
     * @return a list of bookings
     */
    List<Booking> getBookingByBooker_IdOrderByIdDesc(Long booker_id);

    /**
     * Finds all bookings by item owner's ID, with results sorted.
     *
     * @param booker_id the item owner's ID
     * @param sort      the sorting criteria
     * @return a list of bookings
     */
    List<Booking> getBookingByItem_Owner_Id(Long booker_id, Sort sort);

    /**
     * Finds all bookings by item owner's ID that ended before a specific time, sorted.
     *
     * @param booker_id the item owner's ID
     * @param end       the end time to compare
     * @param sort      the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findBookingByItem_Owner_IdAndEndIsBefore(Long booker_id, LocalDateTime end, Sort sort);

    /**
     * Finds all bookings by item owner's ID that start after a specific time, sorted.
     *
     * @param booker_id the item owner's ID
     * @param start     the start time to compare
     * @param sort      the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findBookingByItem_Owner_IdAndStartIsAfter(Long booker_id, LocalDateTime start, Sort sort);

    /**
     * Finds all bookings for an item owner that started before and end after a specified time.
     *
     * @param booker_id the item owner's ID
     * @param start     the start time to compare
     * @param end       the end time to compare
     * @return a list of bookings
     */
    List<Booking> findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long booker_id, LocalDateTime start, LocalDateTime end);

    /**
     * Finds all bookings by booker ID that ended before a specified time, sorted.
     *
     * @param userId the ID of the booker
     * @param end    the end time to compare
     * @param sort   the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    /**
     * Finds all bookings by booker ID that start after a specified time, sorted.
     *
     * @param userId the ID of the booker
     * @param start  the start time to compare
     * @param sort   the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    /**
     * Finds all bookings by a specific status, ordered by booking ID in descending order.
     *
     * @param status the booking status to filter by
     * @return a list of bookings
     */
    List<Booking> findBookingByStatusOrderByIdDesc(BookingState status);

    /**
     * Finds all bookings by booker ID and status, ordered by start time in descending order.
     *
     * @param userId the ID of the booker
     * @param status the booking status to filter by
     * @param sort   the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingState status, Sort sort);

    /**
     * Finds all bookings by item owner's ID and status, ordered by start time in descending order.
     *
     * @param ownerId the ID of the item owner
     * @param status  the booking status to filter by
     * @param sort    the sorting criteria
     * @return a list of bookings
     */
    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingState status, Sort sort);

    /**
     * Finds the first booking for an item that starts after a specific time.
     *
     * @param item_id the item's ID
     * @param start   the start time to compare
     * @return an optional containing the booking if found, or empty if none found
     */
    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long item_id, LocalDateTime start);

    /**
     * Finds the first booking for an item that ended before a specific time.
     *
     * @param item_id the item's ID
     * @param end     the end time to compare
     * @return an optional containing the booking if found, or empty if none found
     */
    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long item_id, LocalDateTime end);

    /**
     * Finds the first booking for an item that started before a specific time and whose status is not the specified one.
     *
     * @param itemId the item's ID
     * @param start  the start time to compare
     * @param status the status to exclude
     * @return an optional containing the booking if found, or empty if none found
     */
    Optional<Booking> findFirstByItem_IdAndStartIsBeforeAndStatusIsNotOrderByStartDesc(Long itemId, LocalDateTime start, BookingState status);

    /**
     * Finds the first booking for an item that starts after a specific time and whose status is not the specified one.
     *
     * @param itemId the item's ID
     * @param start  the start time to compare
     * @param status the status to exclude
     * @return an optional containing the booking if found, or empty if none found
     */
    Optional<Booking> findFirstByItem_IdAndStartIsAfterAndStatusIsNotOrderByStartAsc(Long itemId, LocalDateTime start, BookingState status);
}