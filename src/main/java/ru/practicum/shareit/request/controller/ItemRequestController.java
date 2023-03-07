package ru.practicum.shareit.request.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    //POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
    @PostMapping
    public ItemRequestDto create(@Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto,
                                    @RequestHeader("X-Sharer-User-Id") Long requesterId) {

        return itemRequestService.save(itemRequestDto, requesterId);

    }

    //GET /requests — получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    @GetMapping
    public List<ItemRequestWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
       return itemRequestService.getAllRequests(requesterId);
    }

    //GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями. С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично. Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> findRequestsWithFromAndSize(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                            @RequestParam(required = false, defaultValue = "0") int from,
                                                            @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestService.findAllRequestsByNotId(PageRequest.of(from, size), requesterId);
    }

    // GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                  @PathVariable Long requestId) {
        return itemRequestService.findById(requesterId, requestId);
    }


}
