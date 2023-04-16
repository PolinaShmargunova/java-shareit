package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User add(User user) throws BadRequestException {
        log.info("Добавлен новый пользователь");
        return userStorage.add(user);
    }

    public User getUserById(long id) throws NotFoundException {
        log.info("Получен пользователь с id " + id);
        return userStorage.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAll());
    }

    public User update(User user, long id) {
        log.info("Обновлен пользователь с id " + id);
        return userStorage.update(user,id);
    }

    public void delete(long id) {
        log.info("Удалён пользователь с id " + id);
        userStorage.delete(id);
    }
}
