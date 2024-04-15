package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import static ru.practicum.shareit.booking.BookingState.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = REPEATABLE_READ)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Transactional
    @Override
    public BookingDTOResponse addBooking(BookingDTO bookingDTO, final Long userId) {
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

        Booking booking = bookingMapper.toModel(bookingDTO)
                .toBuilder()
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
    public BookingDTOResponse updateBooking(final Long bookingId, final Boolean approved, final Long userId) {
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

        booking = bookingRepository.save(booking);
        log.info("Booking status updated to {} for booking ID: {}", booking.getStatus(), bookingId);
        return bookingMapper.toDTO(booking);
    }


    @Override
    public BookingDTOResponse getBooking(final Long bookingId, final Long userId) {
        log.debug("Fetching booking with ID: {} for user ID: {}", bookingId, userId);

        final BookingDTOResponse dto = bookingMapper.toDTO(bookingRepository
                .getBookingByIdAndBooker_IdOrIdAndItem_Owner_Id(bookingId, userId, bookingId, userId));
        if (dto == null) {
            log.error("Booking not found with Id: {}", bookingId);
            throw new NotFoundException("Booking not found");
        }

        log.info("Booking retrieved successfully for booking ID: {}", bookingId);
        return dto;
    }

    @Override
    public List<BookingDTOResponse> getBookings(final Long userId, final BookingState state) {
        log.debug("Retrieving bookings for user ID: {} with state: {}", userId, state);

        verifyUser(userId);
        final Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final LocalDateTime now = LocalDateTime.now();
        final List<BookingDTOResponse> bookings;

        switch (state) {
            case ALL: {
                bookings = bookingMapper.toDTOList(bookingRepository.getBookingByBooker_IdOrderByIdDesc(userId));
                break;
            }
            case PAST: {
                bookings = bookingMapper.toDTOList(bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort));
                break;
            }
            case FUTURE: {
                bookings = bookingMapper.toDTOList(bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort));
                break;
            }
            case CURRENT: {
                bookings = bookingMapper.toDTOList(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now));
                break;
            }
            case WAITING: {
                bookings = bookingMapper.toDTOList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING, sort));
                break;
            }
            case REJECTED: {
                bookings = bookingMapper.toDTOList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED, sort));
                break;
            }
            default: {
                log.error("Invalid booking state requested: {}", state);
                throw new UnSupportedStatusException("Unknown state: " + state);
            }
        }

        log.info("Found {} bookings for user ID: {} with state: {}", bookings.size(), userId, state);
        return bookings;
    }


    @Override
    public List<BookingDTOResponse> getOwnerBookings(final Long userId, final BookingState state) {
        log.debug("Retrieving owner bookings for user ID: {} with state: {}", userId, state);

        verifyUser(userId);
        final Sort sort = Sort.by(Sort.Direction.DESC, "id");
        final LocalDateTime now = LocalDateTime.now();
        final List<BookingDTOResponse> bookings;

        switch (state) {
            case ALL: {
                bookings = bookingMapper.toDTOList(bookingRepository.getBookingByItem_Owner_Id(userId, sort));
                break;
            }
            case PAST: {
                bookings = bookingMapper.toDTOList(bookingRepository
                        .findBookingByItem_Owner_IdAndEndIsBefore(userId, now, sort));
                break;
            }
            case FUTURE: {
                bookings = bookingMapper.toDTOList(bookingRepository.findBookingByItem_Owner_IdAndStartIsAfter(userId, now, sort));
                break;
            }
            case CURRENT: {
                bookings = bookingMapper.toDTOList(bookingRepository
                        .findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, now, now));
                break;
            }
            case REJECTED: {
                bookings = bookingMapper.toDTOList(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, REJECTED, sort));
                break;
            }
            case WAITING: {
                bookings = bookingMapper.toDTOList(bookingRepository.findBookingByStatusOrderByIdDesc(WAITING));
                break;
            }
            default: {
                log.error("Unknown booking state requested: {}", state);
                throw new UnSupportedStatusException("Unknown state: " + state);
            }
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