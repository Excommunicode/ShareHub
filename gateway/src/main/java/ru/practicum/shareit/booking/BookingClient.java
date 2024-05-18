package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDTO bookingDTO) {
        return post("", userId, bookingDTO);
    }

    public ResponseEntity<Object> updateBookingStatus(long userId, Long bookingId, boolean approved) {
        String path = String.format("/%d?approved=%s", bookingId, approved);
        return patch(path, userId);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, Long userId) {
        String path = String.format("/%d", bookingId);
        return get(path, userId);
    }

    public ResponseEntity<Object> getBookings(Long userId, BookingState bookingState, Integer from, Integer size) {
        String path = String.format("?state=%s&from=%d&size=%d", bookingState.name(), from, size);
        return get(path, userId);
    }

    public ResponseEntity<Object> getBookingsOwner(Long userId, BookingState bookingState, Integer from, Integer size) {
        String path = String.format("/owner?state=%s&from=%d&size=%d", bookingState, from, size);
        return get(path, userId);
    }
}
