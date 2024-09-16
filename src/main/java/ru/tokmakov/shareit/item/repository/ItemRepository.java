package ru.tokmakov.shareit.item.repository;

import ru.tokmakov.shareit.item.dto.ItemDto;
import ru.tokmakov.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Optional<Item> getById(long itemId);

    Item update(long itemId, Item item);

    List<ItemDto> allItemsFromUser(long userId);

    List<ItemDto> search(String text);
}
