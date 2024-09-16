package ru.tokmakov.shareit.item.model;

import lombok.Data;
import ru.tokmakov.shareit.user.model.User;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}