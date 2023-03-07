package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestDto toDto(ItemRequest itemRequest);
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);
    List<ItemRequestDto> toDtoList(List<ItemRequest> itemRequests);



}
