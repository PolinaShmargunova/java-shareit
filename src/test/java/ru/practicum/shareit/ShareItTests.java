package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase()
class ShareItTests {

	UserService userService;
	@MockBean
	ItemService itemService;
	@Mock
	UserRepository userRepository;
	@MockBean
	ItemRepository itemRepository;
	@MockBean
	BookingRepository bookingRepository;
	@MockBean
	CommentRepository commentRepository;

	User user;
	ItemDto itemDto;

	@BeforeEach
	void beforeEach() {

		userService = new UserService(userRepository);
		itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
		user = new User(1, "Test", "test@test.com");
		itemDto = new ItemDto(1, "TestItem", "DescriptionTest", true,0);
	}



	@Test
	void updateUser() throws BadRequestException, NotFoundException {
		when(userRepository.save(any()))
				.thenReturn(user);
		userService.add(user);

		User newUser = new User(1, "Updated", "test@test.com");
		when(userRepository.save(any()))
				.thenReturn(newUser);
		when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(newUser));
		userService.update(newUser, 1);

		Assertions.assertEquals(userService.getUserById(1), newUser);
	}

	@Test
	void deleteUser() throws BadRequestException {
		when(userRepository.save(any()))
				.thenReturn(user);
		userService.add(user);
		userService.delete(1);

		Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1));
	}
}