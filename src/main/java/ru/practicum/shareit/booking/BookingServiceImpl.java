package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnSupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ru.practicum.shareit.booking.BookingState.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = REPEATABLE_READ, propagation = REQUIRED)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDTOResponse addBooking(BookingDTO bookingDTO, Long userId) {
        log.debug("Attempting to add a new booking for user ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with ID: {}", userId);
            return new NotFoundException("User not found");
        });
        Item item = itemRepository.findById(bookingDTO.getItemId()).orElseThrow(() -> {
            log.error("Item not found with ID: {}", bookingDTO.getItemId());
            return new NotFoundException("Item not found");
        });

        if (!item.getAvailable()) {
            log.warn("Attempted to book an unavailable item with ID: {}", bookingDTO.getItemId());
            throw new BadRequestException("Item is not available");
        }

        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart()) || bookingDTO.getStart().equals(bookingDTO.getEnd())) {
            log.warn("Invalid booking period from {} to {} for user ID: {}", bookingDTO.getStart(), bookingDTO.getEnd(), userId);
            throw new BadRequestException("Booking period is invalid");
        }

        if (item.getOwner().getId().equals(userId)) {
            log.warn("Booking attempt by item owner with user ID: {}", userId);
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking booking = bookingMapper.toModel(bookingDTO).toBuilder()
                .booker(user)
                .item(item)
                .status(WAITING)
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking successfully added with ID: {}", booking.getId());
        return bookingMapper.toDTO(booking);
    }

    @Transactional
    @Override
    public BookingDTOResponse updateBooking(Long bookingId, Boolean approved, Long userId) {
        log.debug("Updating booking status for booking ID: {} by user ID: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found with ID: {}", bookingId);
            return new NotFoundException("Booking not found");
        });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Unauthorized access attempt by user ID: {} for booking ID: {}", userId, bookingId);
            throw new NotFoundException("User not authorized");
        }

        if (approved && booking.getStatus().equals(APPROVED)) {
            log.warn("Redundant approval attempt for already approved booking ID: {}", bookingId);
            throw new BadRequestException("Booking already approved");
        }

        booking.setStatus(approved ? APPROVED : REJECTED);

        bookingRepository.updateBookingsByStatus(booking.getStatus().toString(), bookingId);


        log.info("Booking status updated to {} for booking ID: {}", booking.getStatus(), bookingId);
        return bookingMapper.toDTO(booking);
    }


    @Override
    public BookingDTOResponse getBooking(Long bookingId, Long userId) {
        log.debug("Fetching booking with ID: {} for user ID: {}", bookingId, userId);

        BookingDTOResponse dto = bookingMapper.toDTO(bookingRepository
                .getBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(bookingId, userId, bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found")));

        log.info("Booking retrieved successfully for booking ID: {}", bookingId);
        return dto;
    }

    @Override
    public List<BookingDTOResponse> getBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.debug("Retrieving bookings for user ID: {} with state: {}", userId, state);

        verifyUser(userId);
        List<BookingDTOResponse> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findAllByBooker_Id(userId, from, size));
                break;
            case PAST:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, from, size));
                break;
            case FUTURE:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, pageable));
                break;
            case CURRENT:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, pageable));
                break;
            case WAITING:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING, pageable));
                break;
            case REJECTED:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED, pageable));
                break;
            default:
                log.error("Unknown booking state requested: {}", state);
                throw new UnSupportedStatusException("Unknown state: " + state);
        }
        log.info("Found {} bookings for user ID: {} with state: {}", bookings.size(), userId, state);
        return bookings;
    }


    @Override
    public List<BookingDTOResponse> getOwnerBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.debug("Retrieving owner bookings for user ID: {} with state: {}", userId, state);
        verifyUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<BookingDTOResponse> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(from, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.getBookingByItem_Owner_Id(userId, pageable));
                break;
            case PAST:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findBookingByItem_Owner_IdAndEndIsBefore(userId, now, pageable));
                break;
            case FUTURE:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findBookingByItem_Owner_IdAndStartIsAfter(userId, now, pageable));
                break;
            case CURRENT:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, pageable));
                break;
            case REJECTED:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, REJECTED, pageable));
                break;
            case WAITING:
                bookings = bookingMapper.toDTOList(
                        bookingRepository.findBookingByStatusOrderByIdDesc(WAITING, pageable));
                break;
            default:
                log.error("Unknown booking state request: {}", state);
                throw new UnSupportedStatusException("Unknown state: " + state);

        }
        log.info("Found {} owner bookings for user Id: {} with state: {}", bookings.size(), userId, state);
        return bookings;
    }


    private void verifyUser(Long userId) {
        log.debug("Verifying existence of user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User verification failed for user ID: {}", userId);
            throw new NotFoundException("User not found");
        }

        log.info("User verified successfully with user ID: {}", userId);
    }
}