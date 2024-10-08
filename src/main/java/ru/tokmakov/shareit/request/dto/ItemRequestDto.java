package ru.tokmakov.shareit.request.dto;

import lombok.Data;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private User user;
    private LocalDateTime created;
    private List<Item> items;
}
