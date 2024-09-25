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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Item save(ItemDto itemDto, long userId) {
        User user = userService.findById(userId);

        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Override
    public Item update(long itemId, UpdateItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));

        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("Access denied you didn't create this item");
        }

        updateItemDetails(item, itemDto);

        return itemRepository.save(item);
    }

    private void updateItemDetails(Item item, UpdateItemDto itemDto) {
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
    }

    @Override
    public Item findById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
    }

    @Override
    public List<ItemDto> allItemsFromUser(long userId) {
        userService.findById(userId);
        return itemRepository.allItemsFromUser(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text.toLowerCase());
    }
}
