package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.common.Create;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long requesterId) {

        return itemRequestClient.createRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
       return itemRequestClient.getAllRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequestsWithFromAndSize(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                            @RequestParam(required = false, defaultValue = "0") int from,
                                                            @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestClient.findAllRequestsByNotId(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                  @PathVariable Long requestId) {
        return itemRequestClient.findById(requesterId, requestId);
    }
}
