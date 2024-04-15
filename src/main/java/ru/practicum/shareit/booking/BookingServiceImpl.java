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
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
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
    UserMapper userMapper;
    ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingDTOResponse addBooking(BookingDTO bookingDTO, final Long userId) {
        log.debug("Attempting to add a new booking for user ID: {}", userId);

        validateBookingPeriod(bookingDTO);
        ItemDTO item = getItemDTO(userId, bookingDTO);
        validateItemAvailability(item, bookingDTO);
        validateOwnerBooking(item, userId);

        Booking booking = createBooking(bookingDTO, userId, item);
        booking = bookingRepository.save(booking);

        log.info("Booking successfully added with ID: {}", booking.getId());
        return bookingMapper.toDTO(booking);
    }

    @Transactional
    @Override
    public BookingDTOResponse updateBooking(final Long bookingId, final Boolean approved, final Long userId) {
        log.debug("Updating booking status for booking ID: {} by user ID: {}", bookingId, userId);

        Booking booking = getBooking(bookingId);
        verifyOwnerBooking(booking, userId, bookingId);
        checkAlreadyApproved(booking, approved, userId);

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
        verifyAndLog(userId, state);
        List<BookingDTOResponse> bookings = retrieveBookingsBasedOnState(userId, state, false);
        logRetrieval(userId, state, bookings.size());
        return bookings;
    }

    @Override
    public List<BookingDTOResponse> getOwnerBookings(final Long userId, final BookingState state) {
        verifyAndLog(userId, state);
        List<BookingDTOResponse> bookings = retrieveBookingsBasedOnState(userId, state, true);
        logRetrieval(userId, state, bookings.size());
        return bookings;
    }

    private List<BookingDTOResponse> retrieveBookingsBasedOnState(Long userId, BookingState state, boolean isOwner) {
        Sort sort = getDescendingSort();
        LocalDateTime now = getCurrentTime();

        List<Booking> result;
        switch (state) {
            case ALL:
                result = isOwner ? bookingRepository.getBookingByItem_Owner_Id(userId, sort)
                        : bookingRepository.getBookingByBooker_IdOrderByIdDesc(userId);
                break;
            case PAST:
                result = isOwner ? bookingRepository.findBookingByItem_Owner_IdAndEndIsBefore(userId, now, sort)
                        : bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
                break;
            case FUTURE:
                result = isOwner ? bookingRepository.findBookingByItem_Owner_IdAndStartIsAfter(userId, now, sort)
                        : bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort);
                break;
            case CURRENT:
                result = isOwner ? bookingRepository.findBookingByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, now, now)
                        : bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now);
                break;
            case WAITING:
                result = isOwner ? bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, WAITING, sort)
                        : bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING, sort);
                break;
            case REJECTED:
                result = isOwner ? bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, REJECTED, sort)
                        : bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED, sort);
                break;
            default:
                log.error("Unknown booking state requested: {}", state);
                throw new UnSupportedStatusException("Unknown state: " + state);
        }
        return bookingMapper.toDTOList(result);
    }

    private void verifyAndLog(Long userId, BookingState state) {
        verifyUser(userId);
        log.debug("Retrieving bookings for user ID: {} with state: {}", userId, state);
    }

    private void verifyUser(Long userId) {
        log.debug("Verifying existence of user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User verification failed for user ID: {}", userId);
            throw new NotFoundException("User not found");
        }

        log.info("User verified successfully with user ID: {}", userId);
    }

    private UserDTO getUserDTO(Long userId) {
        log.debug("Retrieving user with ID: {}", userId);

        return userMapper.toDTO(userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with ID: {}", userId);
            return new NotFoundException("User not found");
        }));
    }

    private ItemDTO getItemDTO(Long itemId, BookingDTO bookingDTO) {
        log.debug("Retrieving item with ID: {}", itemId);

        return itemMapper.toDTO(itemRepository.findById(bookingDTO.getItemId()).orElseThrow(() -> {
            log.error("Item not found with ID: {}", bookingDTO.getItemId());
            return new NotFoundException("Item not found");
        }));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.error("Booking not found with ID: {}", bookingId);
            return new NotFoundException("Booking not found");
        });
    }

    private void validateBookingPeriod(BookingDTO bookingDTO) {
        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart()) || bookingDTO.getStart().equals(bookingDTO.getEnd())) {
            log.warn("Invalid booking period from {} to {} for user ID: {}", bookingDTO.getStart(), bookingDTO.getEnd());
            throw new BadRequestException("Booking period is invalid");
        }
    }

    private void validateItemAvailability(ItemDTO item, BookingDTO bookingDTO) {
        if (!item.getAvailable()) {
            log.warn("Attempted to book an unavailable item with ID: {}", bookingDTO.getItemId());
            throw new BadRequestException("Item is not available");
        }
    }

    private void validateOwnerBooking(ItemDTO item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Booking attempt by item owner with user ID: {}", userId);
            throw new NotFoundException("Owner cannot book own item");
        }
    }

    private Booking createBooking(BookingDTO bookingDTO, Long userId, ItemDTO item) {
        UserDTO user = getUserDTO(userId);
        return bookingMapper.toModel(bookingDTO)
                .toBuilder()
                .booker(userMapper.toModel(user))
                .item(itemMapper.toModel(item))
                .status(WAITING)
                .build();
    }

    private void verifyOwnerBooking(Booking booking, Long userId, Long bookingId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Unauthorized access attempt by user ID: {} for booking ID: {}", userId, bookingId);
            throw new NotFoundException("User not owner");
        }
    }

    private void checkAlreadyApproved(Booking booking, boolean approved, Long bookingId) {
        if (approved && booking.getStatus().equals(APPROVED)) {
            log.warn("Redundant approval attempt for already approved booking ID: {}", bookingId);
            throw new BadRequestException("Booking already approved");
        }
    }

    private void logRetrieval(Long userId, BookingState state, int size) {
        log.info("Found {} bookings for user ID: {} with state: {}", size, userId, state);
    }

    private Sort getDescendingSort() {
        return Sort.by(Sort.Direction.DESC, "id");
    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}