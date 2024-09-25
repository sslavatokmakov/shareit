package ru.tokmakov.shareit.item.service;

import ru.tokmakov.shareit.item.dto.ItemDto;
import ru.tokmakov.shareit.item.dto.UpdateItemDto;
import ru.tokmakov.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(ItemDto itemDto, long userId);

    Item update(long itemId, UpdateItemDto itemDto, long userId);

    Item findById(long itemId);

    List<ItemDto> allItemsFromUser(long userId);

    List<ItemDto> search(String text);
}
