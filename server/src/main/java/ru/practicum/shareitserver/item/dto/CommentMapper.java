package ru.practicum.shareitserver.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareitserver.item.model.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );

    }

    public static Comment toComment(CommentDtoShort commentDtoShort) {
        return new Comment(commentDtoShort.getId(),
                commentDtoShort.getText()
               );
    }
}
