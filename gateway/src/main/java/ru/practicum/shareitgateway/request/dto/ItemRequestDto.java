package ru.practicum.shareitgateway.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareitgateway.common.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    private Long requesterId;
    @FutureOrPresent
    private LocalDateTime created;
}

