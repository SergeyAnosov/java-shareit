package ru.practicum.shareitserver.booking.dto;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BookingDtoShort {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    public Long getItemId() {
        return itemId;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}


