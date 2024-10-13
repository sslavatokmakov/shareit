package ru.tokmakov.shareitserver.request.dto;

import lombok.Data;
import ru.tokmakov.shareitserver.item.model.Item;
import ru.tokmakov.shareitserver.user.model.User;

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
