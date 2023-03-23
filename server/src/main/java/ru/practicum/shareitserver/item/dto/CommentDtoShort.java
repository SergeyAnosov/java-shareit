package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class CommentDtoShort {
    private Long id;
    private String text;
}
