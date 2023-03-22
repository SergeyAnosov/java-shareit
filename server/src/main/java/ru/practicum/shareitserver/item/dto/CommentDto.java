package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitserver.item.model.Item;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}
