package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapperShortDTO {
    @Mappings({@Mapping(source = "booker.id", target = "bookerId")})
    BookingShortDTO toDTO(Booking booking);

    List<BookingShortDTO> toDTOs(List<Booking> bookings);
}