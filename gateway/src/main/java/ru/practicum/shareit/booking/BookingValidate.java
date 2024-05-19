package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;

@Service
public class BookingValidate {

    public void validDate(BookingDTO bookingDTO) {
        if (bookingDTO.getEnd().isBefore(bookingDTO.getStart()) || bookingDTO.getStart().equals(bookingDTO.getEnd())) {
            throw new BadRequestException("Booking period is invalid");
        }
    }
}
