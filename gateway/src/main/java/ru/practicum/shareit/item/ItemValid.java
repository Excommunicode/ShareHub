package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemValid {
    private final ItemClient client;

    public ResponseEntity<Object> validText(String text, Integer from, Integer size) {
        if (text == null || text.trim().isEmpty()) {
            log.info("Search text is empty or null. Returning empty list.");
            return ResponseEntity.ok(Collections.emptyList());
        }
        return client.searchByText(text, from, size);
    }
}
