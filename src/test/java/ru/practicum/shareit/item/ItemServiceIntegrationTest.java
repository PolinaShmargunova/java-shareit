package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ItemServiceIntegrationTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    public void setup() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    public void testAddRequest_ExistingUser_ReturnsItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        long userId = 1;
        User user = new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(new ItemRequest());

        ItemRequest result = itemRequestService.addRequest(requestDto, userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testAddRequest_NonExistingUser_ThrowsNotFoundException() {
        ItemRequestDto requestDto = new ItemRequestDto();
        long userId = 1;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(requestDto, userId));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    public void testGetRequests_ExistingUser_ReturnsListOfGetItemRequestDto() {
        long userId = 1;
        User user = new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findAllByRequesterId(userId)).thenReturn(new ArrayList<>());
        Mockito.when(itemRequestRepository.findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created")))
                .thenReturn(new ArrayList<>());

        List<GetItemRequestDto> result = itemRequestService.getRequests(userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByRequesterId(userId);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created"));
    }

    @Test
    public void testGetRequests_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(userId));
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequesterId(Mockito.anyLong());
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAllByRequesterId(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    public void testGetRequestsPageable_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        Integer from = 0;
        Integer size = 10;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestsPageable(userId, from, size));
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAllByRequesterIdIsNot(Mockito.anyLong(), Mockito.any(Pageable.class));
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequesterIdIsNot(Mockito.anyLong());
    }

    @Test
    public void testGetRequestById_ExistingUserAndExistingRequest_ReturnsGetItemRequestDto() {
        long userId = 1;
        long requestId = 1;
        User user = new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(new ItemRequest()));
        Mockito.when(itemRepository.findAllByRequestId(requestId)).thenReturn(new ArrayList<>());

        GetItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(requestId);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByRequestId(requestId);
    }

    @Test
    public void testGetRequestById_NonExistingUser_ThrowsNotFoundException() {
        long userId = 1;
        long requestId = 1;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequestId(Mockito.anyLong());
    }

    @Test
    public void testGetRequestById_ExistingUserAndNonExistingRequest_ThrowsNotFoundException() {
        long userId = 1;
        long requestId = 1;
        User user = new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).findAllByRequestId(Mockito.anyLong());
    }
}
