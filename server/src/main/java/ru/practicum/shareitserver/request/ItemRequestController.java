package ru.practicum.shareitserver.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.common.Create;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareitserver.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto,
                                    @RequestHeader("X-Sharer-User-Id") Long requesterId) {

        return itemRequestService.save(itemRequestDto, requesterId);

    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
       return itemRequestService.getAllRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> findRequestsWithFromAndSize(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                            @RequestParam(required = false, defaultValue = "0") int from,
                                                            @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestService.findAllRequestsByNotId(PageRequest.of(from, size), requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                  @PathVariable Long requestId) {
        return itemRequestService.findById(requesterId, requestId);
    }
}
