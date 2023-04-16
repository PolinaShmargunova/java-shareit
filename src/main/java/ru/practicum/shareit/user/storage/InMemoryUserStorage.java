package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> userMap = new HashMap<>();
    long id = 1;

    @Override
    public User add(User user) throws BadRequestException {

        if (user.getEmail() == null) {
            throw new BadRequestException();
        }
        user.setId(id);
        if (!isUniqueEmail(user.getEmail(), user.getId())) {
            throw new ConflictException();
        }
        userMap.put(user.getId(), user);
        id++;
        return userMap.get(user.getId());
    }

    @Override
    public User get(long id) throws NotFoundException {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else throw new NotFoundException();
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    @Override
    public User update(User user, long id) {
        user.setId(id);
        if (user.getName() == null) {
            user.setName(userMap.get(id).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userMap.get(id).getEmail());
        }
        if (!isUniqueEmail(user.getEmail(), user.getId())) {
            throw new ConflictException();
        }
        userMap.put(id, user);
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
