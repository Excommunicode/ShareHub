package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

@Service
public class ItemClient extends BaseClient {
    private static final String API_FLEX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_FLEX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createItem(Long userId, ItemDTO item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDTO item) {
        String path = String.format("/%d", itemId);
        return patch(path, userId, item);
    }

    public ResponseEntity<Object> getById(Long itemId, Long userId) {
        String path = String.format("/%d", itemId);
        return get(path, userId);
    }


    public ResponseEntity<Object> searchByText(String text, Integer from, Integer size) {
        String url = String.format("/search?text=%s&from=%d&size=%d", text, from, size);

        return get(url);
    }

    public ResponseEntity<Object> getAllItem(Long userId, Integer from, Integer size) {
        String url = String.format("?from=%d&size=%d", from, size);

        return get(url, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentDTO comment) {
        String path = String.format("/%d/comment", itemId);
        return post(path, userId, comment);
    }
}
