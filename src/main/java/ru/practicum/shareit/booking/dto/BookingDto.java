package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookingDto {
    private Long id;
    @NotNull(groups = {Create.class})
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
    @NotNull(groups = {Create.class})
    private Long itemId;
    @NotNull(groups = {Create.class})
    private Long bookerId;
    @NotNull(groups = {Create.class})
    private BookingStatus status;

}
