package ru.practicum.shareitgateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.BaseClient;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        return post("", requesterId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllRequests(Long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> findAllRequestsByNotId(Long requesterId, int from, int size) {
        return get("/all?from={from}&size={size}", requesterId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> findById(Long requesterId, Long requestId) {
        return get("/" + requestId, requesterId);
    }
}
