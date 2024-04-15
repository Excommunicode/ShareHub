package ru.practicum.shareit.booking;


import java.util.List;

public interface BookingService {
    BookingDTOResponse addBooking(BookingDTO bookingDTO, Long userId);

    BookingDTOResponse updateBooking(Long bookingId, Boolean approved, Long userId);

    BookingDTOResponse getBooking(Long bookingId, Long userId);

    List<BookingDTOResponse> getBookings(Long userId, BookingState state);

    List<BookingDTOResponse> getOwnerBookings(Long userId, BookingState state);
}
