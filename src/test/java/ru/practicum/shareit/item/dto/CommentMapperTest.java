package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private final User user = new User(1L, "Имя", "email@email.com");
    private final Item item = new Item(1L, "item", "desc",  true, user, 1L);
    Comment comment = new Comment(1L,"Текст", item, user,
            LocalDateTime.of(2024, 1, 1, 1, 1, 1)
    );

    @Test
    void toCommentDto() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem(), commentDto.getItem());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void toComment() {
        CommentDtoShort commentDtoShort = new CommentDtoShort(1L,"comment");

        Comment comment = CommentMapper.toComment(commentDtoShort);

        assertEquals(commentDtoShort.getId(), comment.getId());
        assertEquals(commentDtoShort.getText(), comment.getText());
    }
}