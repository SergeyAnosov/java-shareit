package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId);

    List<ItemRequestWithItemsDto> getAllRequests(Long requesterId);

    ItemRequestWithItemsDto findById(Long requesterId, Long requestId);

    List<ItemRequestWithItemsDto> findAllRequestsByNotId(PageRequest pageRequest, Long requesterId);
}

