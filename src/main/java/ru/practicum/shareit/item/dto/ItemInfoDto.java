package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
