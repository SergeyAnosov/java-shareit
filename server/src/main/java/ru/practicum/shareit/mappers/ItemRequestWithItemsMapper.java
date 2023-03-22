package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestWithItemsMapper {

    ItemRequestWithItemsMapper INSTANCE = Mappers.getMapper(ItemRequestWithItemsMapper.class);

    ItemRequestWithItemsDto toDto(ItemRequest itemRequest);
}
