package ru.tokmakov.shareit.user.service;

import ru.tokmakov.shareit.user.dto.UserDto;
import ru.tokmakov.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User getById(long userId);

    List<User> getAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
