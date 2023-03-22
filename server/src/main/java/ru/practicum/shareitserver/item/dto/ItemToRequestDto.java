package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemToRequestDto {
    private Long id;
    private String name;
    private Long ownerId;
}
