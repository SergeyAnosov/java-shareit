package ru.practicum.shareitserver.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareitserver.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareitserver.request.model.ItemRequest;

@Mapper
public interface ItemRequestWithItemsMapper {

    ItemRequestWithItemsMapper INSTANCE = Mappers.getMapper(ItemRequestWithItemsMapper.class);

    ItemRequestWithItemsDto toDto(ItemRequest itemRequest);
}
