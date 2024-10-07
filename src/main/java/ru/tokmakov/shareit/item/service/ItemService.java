package ru.tokmakov.shareit.item.service;

import ru.tokmakov.shareit.item.dto.*;
import ru.tokmakov.shareit.item.model.Comment;
import ru.tokmakov.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(ItemDto itemDto, long userId);

    Item update(long itemId, UpdateItemDto itemDto, long userId);

    ItemWithCommentsDto findById(long itemId);

    List<ItemWithBookingDateDto> allItemsFromUser(long userId);

    List<ItemDto> search(String text);

    CommentDto saveComment(Comment comment, long itemId, long userId);
}
