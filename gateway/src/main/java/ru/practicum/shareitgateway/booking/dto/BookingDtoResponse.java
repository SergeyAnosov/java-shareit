package ru.practicum.shareitgateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareitgateway.booking.BookingStatus;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private ItemDto item;
    private BookingStatus status;
}
