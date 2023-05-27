package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
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

    @Test
    void addItem() throws NotFoundException {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.save(any()))
                .thenReturn(ItemMapper.toItem(itemDto, itemId));

        assertEquals(itemDto.getName(), itemService.addItem(itemDto, 1).getName());
    }

    @Test
    void addItemNoUser() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, itemId));
    }

    @Test
    void patchItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        when(itemRepository.getReferenceById(itemId))
                .thenReturn(item);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemDto, itemService.patchItem(itemDto, ownerId, itemId));
    }

    @Test
    void patchItemNotFound() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 2L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);

        assertThrows(NullPointerException.class, () -> itemService.patchItem(itemDto, ownerId, itemId));
    }

    @Test
    void patchItemWithIdOnly() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        ItemDto newDto = new ItemDto(itemId, null, null, null, 0);
        when(itemRepository.getReferenceById(itemId))
                .thenReturn(item);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemDto, itemService.patchItem(newDto, ownerId, itemId));
    }

    @Test
    void bookingsIsNotEmptyGetItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        GetItemDto getItemDto = ItemMapper.toGetItemDto(item, null, null, Collections.emptyList());

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setStatus(Status.WAITING);

        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.allBookingsForItem(anyLong(), any()))
                .thenReturn(List.of(booking));

        assertNotEquals(getItemDto, itemService.getItem(itemId, ownerId));
    }

    @Test
    void getItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        GetItemDto getItemDto = ItemMapper.toGetItemDto(item, null, null, Collections.emptyList());

        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.allBookingsForItem(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        assertEquals(getItemDto, itemService.getItem(itemId, ownerId));
    }

    @Test
    void getItemNotFound() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        GetItemDto getItemDto = ItemMapper.toGetItemDto(item, null, null, Collections.emptyList());

        when(itemRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, ownerId));
    }

    @Test
    void getAllItemsByOwner() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        GetItemDto getItemDto = ItemMapper.toGetItemDto(item, null, null, Collections.emptyList());
        final Page<Item> page = new PageImpl<>(List.of(item));
        when(itemRepository.findAll((Pageable) any()))
                .thenReturn(page);

        when(commentRepository.findAllByItemsOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemsOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());

        assertEquals(List.of(getItemDto), itemService.getAllItemsByOwner(ownerId, 0, 10));
    }

    @Test
    void searchItem() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);

        when(itemRepository.search(anyString(), any()))
                .thenReturn(List.of(item));

        assertEquals(List.of(itemDto), itemService.searchItem("test", ownerId, 0, 10));
    }

    @Test
    void searchItemEmptyText() throws NotFoundException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);

        assertEquals(List.of(), itemService.searchItem("", ownerId, 0, 10));
    }

    @Test
    void addComment() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthorName("test");
        comment.setAuthorId(1);
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), itemId, 1, Status.APPROVED);
        when(bookingRepository.bookingsForItemAndBookerPast(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1, "test", "test@test.com")));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertEquals(comment.getText(), itemService.addComment(comment, itemId, 1).getText());
    }

    @Test
    void addCommentNoBooking() throws NotFoundException, BadRequestException {
        long itemId = 1L;
        long ownerId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "TestItem", "DescriptionTest", true, 0);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthorName("test");
        comment.setAuthorId(1);
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), itemId, 1, Status.APPROVED);
        when(bookingRepository.bookingsForItemAndBookerPast(anyLong(), anyLong(), any()))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.addComment(comment, itemId, 1));
    }
}