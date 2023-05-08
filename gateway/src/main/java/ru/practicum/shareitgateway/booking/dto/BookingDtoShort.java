package ru.practicum.shareitgateway.booking.dto;

import lombok.*;
import ru.practicum.shareitgateway.common.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BookingDtoShort {
    private Long id;
    @NotNull(groups = {Create.class})
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
    @NotNull
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


