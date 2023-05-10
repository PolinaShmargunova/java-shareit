package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User add(User user) throws ConflictException {
        try {
            log.info("Добавлен новый пользователь");
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ConflictException("Такой пользователь уже существует");
        }

    }
    public User getUserById(long id) throws NotFoundException {

        if (userRepository.findById(id).isPresent()) {
            log.info("Получен пользователь с id " + id);
            return userRepository.findById(id).get();
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User update(User user, long id) throws NotFoundException {
        user.setId(id);
        if (user.getName() == null) {
            if (userRepository.findById(id).isPresent()) {
                user.setName(userRepository.findById(id).get().getName());
            } else {
                throw new NotFoundException("Обновление невозможно - выбранный пользователь не существует");
            }
        }
        if (user.getEmail() == null) {
            if (userRepository.findById(id).isPresent()) {
                user.setEmail(userRepository.findById(id).get().getEmail());
            } else {
                throw new NotFoundException("Обновление невозможно - выбранный пользователь не существует");
            }
        }
        log.info("Обновлен пользователь с id " + id);
        return userRepository.save(user);
    }

    public void delete(long id) {
        log.info("Удалён пользователь с id " + id);
        userRepository.deleteById(id);
    }
}