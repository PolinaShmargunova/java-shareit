package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addNewUser() throws BadRequestException, NotFoundException {
        long userId = 0L;
        User expectedUser = new User(userId, "Test", "test@test.com");
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.add(expectedUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void addNewUserDuplicateEmail() throws BadRequestException, NotFoundException {
        long userId = 0L;
        User expectedUser = new User(userId, "Test", "test@test.com");
        when(userRepository.save(any()))
                .thenThrow(ConflictException.class);

        assertThrows(ConflictException.class, () -> userService.add(expectedUser));
    }

    @Test
    void getUserById() {
        long userId = 0L;
        UserDto dto = new UserDto(userId, "Test", "test@test.com");
        User expectedUser = UserMapper.toUser(dto);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        User actualUser = userService.getUserById(userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByIdNotFound() {
        long userId = 0L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUserByIdNotFound() {
        long userId = 0L;
        UserDto dto = new UserDto(userId, null, "test@test.com");
        User expectedUser = UserMapper.toUser(dto);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(expectedUser, 1));
    }

    @Test
    void updateUserByIdNotFoundNoEmail() {
        long userId = 0L;
        UserDto dto = new UserDto(userId, "test", null);
        User expectedUser = UserMapper.toUser(dto);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(expectedUser, 1));
    }

    @Test
    void getAllUsers() {
        long userId = 0L;
        User expectedUser = new User(userId, "Test", "test@test.com");
        when(userRepository.findAll())
                .thenReturn(List.of(expectedUser));
        List<User> actualUser = userService.getAllUsers();
        assertEquals(List.of(expectedUser), actualUser);
    }

    @Test
    void updateUserByIdNoEmail() {
        long userId = 1L;
        User expectedUser = new User(userId, "Test", "test@test.com");
        User noEmailUser = new User(userId, "Test", null);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.update(noEmailUser, userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUserByIdNoName() {
        long userId = 1L;
        User expectedUser = new User(userId, "Test", "test@test.com");
        User noEmailUser = new User(userId, null, "test@test.com");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.update(noEmailUser, userId);
        assertEquals(expectedUser, actualUser);
    }
}