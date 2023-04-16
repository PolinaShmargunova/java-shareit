package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User add(@Valid @RequestBody User user) throws BadRequestException {
        log.info("Получен запрос POST /users");
        return userService.add(user);
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable long id) throws NotFoundException {
        log.info("Получен запрос GET /users/" + id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @PatchMapping(value = "/{id}")
    public User update(@RequestBody User user, @PathVariable long id) {
        log.info("Получен запрос PATCH /users/" + id);
        return userService.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable long id) {
        log.info("Получен запрос DELETE /users/" + id);
        userService.delete(id);
    }
}
