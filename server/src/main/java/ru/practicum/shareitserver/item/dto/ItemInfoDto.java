package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareitserver.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    public boolean getAvailable() {
        return available;
    }
}
