package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto read(long userId);

    Collection<UserDto> readAll();

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

    void userIsExist(long userId);

    User getUserById(long userId);

}
