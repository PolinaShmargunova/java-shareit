package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.debug("Create");
        return userService.create(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto read(@PathVariable long userId) {
        log.debug("Read({})", userId);
        return userService.read(userId);
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        log.debug("ReadAll");
        return userService.readAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @RequestBody UserDto userDto) {
        log.debug("Update({})", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.debug("Delete({})", userId);
        userService.delete(userId);
    }
}
