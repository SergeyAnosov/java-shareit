package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.mappers.ItemRequestWithItemsMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId) {

        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestDto);
        User requester = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с таким id не найден"));
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        ItemRequestDto response = ItemRequestMapper.INSTANCE.toDto(itemRequest);
        response.setRequesterId(requesterId);
        return response;
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllRequests(Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        List<ItemRequestWithItemsDto> response = new ArrayList<>();
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreated(requesterId);
        for (ItemRequest ir : requests) {
            List<ItemDto> items = new ArrayList<>();
            List<ItemDto> byRequestId = getItemsListByRequestId(ir.getId());
            items.addAll(byRequestId);
            ItemRequestWithItemsDto responseDto = ItemRequestWithItemsMapper.INSTANCE.toDto(ir);
            responseDto.setItems(items);
            response.add(responseDto);
        }
        return response;
    }

    @Override
    public List<ItemRequestWithItemsDto> findAllRequestsByNotId(PageRequest pageRequest, Long requesterId) {

        Page<ItemRequest> allByRequesterIdNot = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(pageRequest, requesterId);

        List<ItemRequest> requests = allByRequesterIdNot.getContent();
        List<ItemRequestWithItemsDto> response = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            List<ItemDto> items = getItemsListByRequestId(itemRequest.getId());
            ItemRequestWithItemsDto i = ItemRequestWithItemsMapper.INSTANCE.toDto(itemRequest);
            i.setItems(items);
            response.add(i);
        }
        return response;
    }

    @Override
    public ItemRequestWithItemsDto findById(Long requesterId, Long requestId) {
        userRepository.findById(requesterId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                    "пользователя не существует"));

        ItemRequest ir = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "запроса не существует"));
        ItemRequestWithItemsDto response = ItemRequestWithItemsMapper.INSTANCE.toDto(ir);
        response.setItems(getItemsListByRequestId(requestId));
        return response;
    }

    private List<ItemDto> getItemsListByRequestId(Long requestId) {
        List<Item> list = itemRepository.findByRequestId(requestId);
        return list.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}

