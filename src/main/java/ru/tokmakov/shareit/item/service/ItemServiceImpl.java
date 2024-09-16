package ru.tokmakov.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tokmakov.shareit.item.dto.ItemDto;
import ru.tokmakov.shareit.item.dto.ItemMapper;
import ru.tokmakov.shareit.item.dto.UpdateItemDto;
import ru.tokmakov.shareit.item.exception.AccessDeniedException;
import ru.tokmakov.shareit.item.exception.ItemNotFoundException;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.item.repository.ItemRepository;
import ru.tokmakov.shareit.user.model.User;
import ru.tokmakov.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private long id = 0;

    @Override
    public Item create(ItemDto itemDto, long userId) {
        User user = userService.getById(userId);

        Item item = ItemMapper.dtoToItem(itemDto);
        item.setId(generateId());
        item.setOwner(user);

        itemRepository.create(item);
        return item;
    }

    @Override
    public Item update(long itemId, UpdateItemDto itemDto, long userId) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("Access denied you didn't create this item");
        }
        updateItemDetails(item, itemDto);
        return itemRepository.update(itemId, item);
    }

    private void updateItemDetails(Item item, UpdateItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    @Override
    public Item getById(long itemId) {
        return itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
    }

    @Override
    public List<ItemDto> allItemsFromUser(long userId) {
        userService.getById(userId);
        return itemRepository.allItemsFromUser(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text);
    }

    private long generateId() {
        return ++id;
    }
}
