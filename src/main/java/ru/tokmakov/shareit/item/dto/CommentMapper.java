package ru.tokmakov.shareit.item.dto;

import ru.tokmakov.shareit.item.model.Comment;

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
