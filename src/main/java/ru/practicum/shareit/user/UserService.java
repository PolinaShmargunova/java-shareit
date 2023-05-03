package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User add(UserDto dto) throws BadRequestException {
        log.info("Добавлен новый пользователь");
        return userStorage.add(dto);
    }

    public User getUserById(long id) throws NotFoundException {
        log.info("Получен пользователь с id " + id);
        return userStorage.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAll());
    }

    public User update(UserDto dto, long id) {
        log.info("Обновлен пользователь с id " + id);
        return userStorage.update(dto,id);
    }

    public void delete(long id) {
        log.info("Удалён пользователь с id " + id);
        userStorage.delete(id);
    }
}
