package ru.practicum.shareitgateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class CommentDtoShort {
    private Long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
