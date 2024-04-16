package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.booking.BookingConstant.ALL;
import static ru.practicum.shareit.item.ItemConstant.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDTOResponse createBooking(@RequestHeader(X_SHARER_USER_ID) final Long userid,
                                            @Valid @RequestBody BookingDTO bookingDTO) {
        return bookingService.addBooking(bookingDTO, userid);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDTOResponse updateBookingStatus(@PathVariable final Long bookingId,
                                                  @RequestParam final Boolean approved,
                                                  @RequestHeader(X_SHARER_USER_ID) final Long ownerId) {
        return bookingService.updateBooking(bookingId, approved, ownerId);
    }


    @GetMapping("/{bookingId}")
    public BookingDTOResponse getBooking(@PathVariable final Long bookingId,
                                         @RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDTOResponse> getBookings(@RequestHeader(X_SHARER_USER_ID) final Long userId,
                                                @RequestParam(defaultValue = ALL) final BookingState state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDTOResponse> getBookingsOwner(
            @RequestHeader(X_SHARER_USER_ID) final Long userId,
            @RequestParam(defaultValue = ALL) final BookingState state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}