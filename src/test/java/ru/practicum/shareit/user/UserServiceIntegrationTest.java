package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void testAddUser() {
        User user = new User(1L, "John Doe", "john@example.com");
        User addedUser = userService.add(user);

        assertNotNull(addedUser.getId());
        assertEquals(user.getName(), addedUser.getName());
        assertEquals(user.getEmail(), addedUser.getEmail());
    }

    @Test
    public void testGetUserById() throws NotFoundException {
        User user = new User(1L, "John Doe", "john@example.com");
        userRepository.save(user);

        User retrievedUser = userService.getUserById(user.getId());

        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testGetUserById_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(9999L));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(2L, "Jane Smith", "jane@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertFalse(users.contains(user1));
        assertFalse(users.contains(user2));
    }

    @Test
    public void testUpdateUser() throws NotFoundException {
        User user = new User(1L, "John Doe", "john@example.com");
        userRepository.save(user);

        User updatedUser = new User(2L, "Updated Name", null);
        User result = userService.update(updatedUser, user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User user = new User(1L, "John Doe", "john@example.com");
        userRepository.save(user);

        userService.delete(user.getId());

        assertFalse(userRepository.existsById(user.getId()));
    }
}
