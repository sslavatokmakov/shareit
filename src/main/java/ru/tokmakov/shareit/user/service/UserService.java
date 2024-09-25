package ru.tokmakov.shareit.user.service;

import ru.tokmakov.shareit.user.dto.UserDto;
import ru.tokmakov.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User findById(long userId);

    List<User> findAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
