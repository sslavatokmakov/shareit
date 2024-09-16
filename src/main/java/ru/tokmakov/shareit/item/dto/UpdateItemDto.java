package ru.tokmakov.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemDto {
    private String name;
    private String description;
    private Boolean available;
}
