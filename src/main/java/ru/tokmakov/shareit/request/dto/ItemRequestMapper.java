package ru.tokmakov.shareit.request.dto;

import ru.tokmakov.shareit.request.model.ItemRequest;

public class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setUser(itemRequest.getUser());
        itemRequestDto.setDescription(itemRequest.getDescription());
        return itemRequestDto;
    }
}
