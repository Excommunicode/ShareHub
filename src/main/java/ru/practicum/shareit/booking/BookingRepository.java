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
     * Checks if there is any booking that matches the given booker ID, item ID, list of statuses, and is ending before a specified date.
     *
     * @param bookerId  the ID of the booker
     * @param itemId    the ID of the item booked
     * @param status    a list of booking states
     * @param endBefore the datetime before which the booking should end
     * @return true if such a booking exists, false otherwise
     */
    boolean existsByBookerIdAndItemIdAndStatusInAndEndBefore(Long bookerId, Long itemId, List<BookingState> status, LocalDateTime endBefore);

    /**
     * Retrieves a booking based on a complex query that matches either the booking ID and booker ID or another booking ID and item owner ID.
     *
     * @param id       the first booking ID to match
     * @param bookerId the booker ID to match
     * @param id2      the second booking ID to match
     * @param itemIdu  the item owner ID to match
     * @return the found booking, or null if no booking matches the criteria
     */
    Booking getBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(Long id, Long bookerId, Long id2, Long itemIdu);

    /**
     * Finds all bookings for a booker where the booking starts before and ends after specified datetime.
     *
     * @param bookerId the ID of the booker
     * @param start    the datetime before which the booking starts
     * @param end      the datetime after which the booking ends
     * @return a list of bookings matching the criteria
     */
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves all bookings made by a specific booker, ordered by booking ID in descending order.
     *
     * @param bookerId the ID of the booker
     * @return a list of bookings ordered by ID in descending order
     */
    List<Booking> getBookingByBooker_IdOrderByIdDesc(Long bookerId);

    /**
     * Finds all bookings for an item's owner, ordered as specified.
     *
     * @param bookerId the ID of the item owner
     * @param sort     the sorting criteria
     * @return a sorted list of bookings
     */
    List<Booking> getBookingByItem_Owner_Id(Long bookerId, Sort sort);

    /**
     * Finds all bookings for an item's owner that end before a specified datetime, sorted as specified.
     *
     * @param bookerId the ID of the item owner
     * @param end      the end datetime to compare
     * @param sort     the sorting criteria
     * @return a sorted list of bookings that end before the specified datetime
     */
    List<Booking> findBookingByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    /**
     * Finds all bookings for an item's owner that start after a specified datetime.
     *
     * @param bookerId the ID of the item owner
     * @param start    the start datetime to compare
     * @param sort     the sorting criteria
     * @return a sorted list of bookings that start after the specified datetime
     */
    List<Booking> findBookingByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    /**
     * Finds all bookings for an item's owner that start before and end after specified datetime.
     *
     * @param bookerId the ID of the item owner
     * @param start    the datetime before which the booking starts
     * @param end      the datetime after which the booking ends
     * @return a list of bookings meeting the specified criteria
     */
    List<Booking> findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end);

    /**
     * Finds all bookings for a booker that end before a specified datetime, sorted as specified.
     *
     * @param userId the ID of the booker
     * @param end    the end datetime to compare
     * @param sort   the sorting criteria
     * @return a sorted list of bookings that end before the specified datetime
     */
    List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    /**
     * Finds all bookings for a booker that start after a specified datetime, sorted as specified.
     *
     * @param userId the ID of the booker
     * @param start  the start datetime to compare
     * @param sort   the sorting criteria
     * @return a sorted list of bookings that start after the specified datetime
     */
    List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    /**
     * Finds all bookings with a specific status, ordered by booking ID in descending order.
     *
     * @param status the booking state to filter by
     * @return a list of bookings with the specified status, ordered by ID in descending order
     */
    List<Booking> findBookingByStatusOrderByIdDesc(BookingState status);

    /**
     * Finds all bookings for a booker with a specific status, ordered by the start date in descending order.
     *
     * @param userId the ID of the booker
     * @param status the booking state to filter by
     * @param sort   the sorting criteria
     * @return a sorted list of bookings with the specified status
     */
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingState status, Sort sort);

    /**
     * Finds all bookings for an item owner with a specific status, ordered by the start date in descending order.
     *
     * @param ownerId the ID of the item owner
     * @param status  the booking state to filter by
     * @param sort    the sorting criteria
     * @return a sorted list of bookings with the specified status
     */
    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingState status, Sort sort);

    /**
     * Retrieves the first booking for an item that starts after a specified datetime, ordered by the start date ascending.
     *
     * @param itemId the ID of the item
     * @param start  the start datetime to compare
     * @return an optional containing the first booking if found, or empty if no such booking exists
     */
    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    /**
     * Retrieves the first booking for an item that ends before a specified datetime, ordered by the end date descending.
     *
     * @param itemId the ID of the item
     * @param end    the end datetime to compare
     * @return an optional containing the first booking if found, or empty if no such booking exists
     */
    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    /**
     * Retrieves the first booking for an item that starts before a specified datetime and whose status is not as specified, ordered by start descending.
     *
     * @param itemId the ID of the item
     * @param start  the start datetime to compare
     * @param status the booking state not to match
     * @return an optional containing the first booking if found, or empty if no such booking exists
     */
    Optional<Booking> findFirstByItem_IdAndStartIsBeforeAndStatusIsNotOrderByStartDesc(Long itemId, LocalDateTime start, BookingState status);

    /**
     * Retrieves the first booking for an item that starts after a specified datetime and whose status is not as specified, ordered by start ascending.
     *
     * @param itemId the ID of the item
     * @param start  the start datetime to compare
     * @param status the booking state not to match
     * @return an optional containing the first booking if found, or empty if no such booking exists
     */
    Optional<Booking> findFirstByItem_IdAndStartIsAfterAndStatusIsNotOrderByStartAsc(Long itemId, LocalDateTime start, BookingState status);


}