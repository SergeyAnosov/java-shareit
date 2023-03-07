package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto, Long userId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId()
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item) {
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static ItemToRequestDto toItemRequestDto(Item item) {
        return new ItemToRequestDto(item.getId(),
                    item.getName(),
                    item.getOwner().getId()
                );
    }
}
