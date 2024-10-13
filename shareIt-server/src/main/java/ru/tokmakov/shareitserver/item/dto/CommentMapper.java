package ru.tokmakov.shareitserver.item.dto;

import ru.tokmakov.shareitserver.item.model.Comment;

public class CommentMapper {
    private CommentMapper() {
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getItem(),
                comment.getCreated());
    }
}
