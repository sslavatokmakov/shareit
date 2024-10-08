package ru.tokmakov.shareit.request.service;

import ru.tokmakov.shareit.request.dto.ItemRequestDto;
import ru.tokmakov.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest save(ItemRequest itemRequest, long userId);

    List<ItemRequestDto> findByUserId(long userId);

    List<ItemRequest> findAll(long userId);

    ItemRequestDto findById(long requestId);
}
