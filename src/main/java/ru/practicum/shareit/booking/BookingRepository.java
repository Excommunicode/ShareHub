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

    boolean existsByBookerIdAndItemIdAndStatusInAndEndBefore(Long bookerId, Long itemId, List<BookingState> status, LocalDateTime endBefore);

    Booking getBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(Long id, Long booker_id, Long id2, Long item_id);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long booker_id, LocalDateTime start, LocalDateTime end);

    List<Booking> getBookingByBooker_IdOrderByIdDesc(Long booker_id);

    List<Booking> getBookingByItem_Owner_Id(Long booker_id, Sort sort);

    List<Booking> findBookingByItem_Owner_IdAndEndIsBefore(Long booker_id, LocalDateTime end, Sort sort);

    List<Booking> findBookingByItem_Owner_IdAndStartIsAfter(Long booker_id, LocalDateTime start, Sort sort);

    List<Booking> findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long booker_id, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findBookingByStatusOrderByIdDesc(BookingState status);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingState status, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingState status, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(Long item_id, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(Long item_id, LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndStartIsBeforeAndStatusIsNotOrderByStartDesc(Long itemId, LocalDateTime start, BookingState status);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterAndStatusIsNotOrderByStartAsc(Long itemId, LocalDateTime start, BookingState status);
}