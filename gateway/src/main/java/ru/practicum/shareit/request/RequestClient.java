package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, RequestDTO requestDTO) {
        return post("", userId, requestDTO);
    }

    public ResponseEntity<Object> getRequests(Long userId, Integer from, Integer size) {
        String url = String.format("?from=%d&size=%d", from, size);
        return get(url, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        String url = String.format("/all?from=%d&size=%d", from, size);
        return get(url, userId);
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {
        String url = String.format("/%d", requestId);
        return get(url, userId);
    }

}
