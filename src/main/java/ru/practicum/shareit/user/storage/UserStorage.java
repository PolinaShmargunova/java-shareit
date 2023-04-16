package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user) throws BadRequestException;

    User get(long id) throws NotFoundException;

    Collection<User> getAll();

    User update(User user, long id);

    void delete(long id);
}
