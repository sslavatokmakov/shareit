package ru.tokmakov.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.tokmakov.shareit.item.dto.ItemDto;
import ru.tokmakov.shareit.item.dto.ItemMapper;
import ru.tokmakov.shareit.item.dto.UpdateItemDto;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public Item save(@RequestBody @Valid ItemDto itemDto,
                     @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: create is called");
        Item item = itemService.save(itemDto, userId);
        log.info("ItemController: item created successfully");
        return item;
    }

    @PatchMapping("{itemId}")
    public Item update(@PathVariable long itemId,
                       @RequestBody UpdateItemDto itemDto,
                       @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: update is called");
        Item item = itemService.update(itemId, itemDto, userId);
        log.info("ItemController: item updated successfully");
        return item;
    }

    @GetMapping("{itemId}")
    public ItemDto findById(@PathVariable long itemId) {
        log.info("ItemController: getById is called");
        ItemDto itemDto = ItemMapper.itemToDto(itemService.findById(itemId));
        log.info("ItemController: item received successfully");
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> allItemsFromUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("ItemController: allItemsFromUser is called");
        List<ItemDto> items = itemService.allItemsFromUser(userId);
        log.info("ItemController: all items from user successfully received");
        return items;
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("ItemController: search is called");
        List<ItemDto> items = itemService.search(text);
        log.info("ItemController: search is successfully");
        return items;
    }
}