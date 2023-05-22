package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItem_existingUser_returnItemDto() throws NotFoundException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        long ownerId = 1L;
//        User user = new User();
//        user.setId(ownerId);
//        user.setName("name");
//        user.setEmail("test@example.com");

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(new Item());

        ItemDto result = itemService.addItem(itemDto, ownerId);

        assertNotNull(result);
    }

    @Test
    void testAddItem_nonExistingUser_throwNotFoundException() throws NotFoundException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        long ownerId = 1L;

        when(userRepository.existsById(ownerId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, ownerId));
    }

    @Test
    void testGetAllItemsByOwner_validInput_returnListOfGetItemDto() {
        long ownerId = 1L;
        int from = 0;
        int size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());

        Page<Item> itemPage = new PageImpl<>(itemList);

        when(itemRepository.findAll(any(PageRequest.class))).thenReturn(itemPage);
        when(commentRepository.findAllByItemsOwnerId(ownerId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemsOwnerId(ownerId)).thenReturn(Collections.emptyList());

        List<GetItemDto> result = itemService.getAllItemsByOwner(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

