package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> userMap = new HashMap<>();
    long id = 1;

    @Override
    public User add(UserDto dto) throws BadRequestException {

        if (dto.getEmail() == null) {
            throw new BadRequestException();
        }
        dto.setId(id);
        if (!isUniqueEmail(dto.getEmail(), dto.getId())) {
            throw new ConflictException();
        }
        userMap.put(dto.getId(), toUser(dto));
        id++;
        return userMap.get(dto.getId());
    }

    @Override
    public User get(long id) throws NotFoundException {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else throw new NotFoundException("Данный пользователь не найден");
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    @Override
    public User update(UserDto dto, long id) {
        dto.setId(id);
        if (dto.getName() == null) {
            dto.setName(userMap.get(id).getName());
        }
        if (dto.getEmail() == null) {
            dto.setEmail(userMap.get(id).getEmail());
        }
        if (!isUniqueEmail(dto.getEmail(), dto.getId())) {
            throw new ConflictException();
        }
        userMap.put(id, toUser(dto));
        return userMap.get(id);
    }

    @Override
    public void delete(long id) {
        userMap.remove(id);
    }

    public boolean isUniqueEmail(String email, long id) {
        List<User> list = new ArrayList<>(getAll());

        for (User user : list) {
            if (email.equals(user.getEmail()) && user.getId() != id) {
                return false;
            }
        }
        return true;
    }
}
