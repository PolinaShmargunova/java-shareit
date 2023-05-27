package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addRequest(ItemRequestDto request, long userId) {
        if (userRepository.existsById(userId)) {
            return itemRequestRepository.save(ItemRequestMapper.toItemRequest(request, userId));
        } else {
            throw new NotFoundException("Не удалось добавить ответ");
        }
    }

    @Override
    public List<GetItemRequestDto> getRequests(long userId) {
        if (userRepository.existsById(userId)) {
            List<ItemDto> items = itemRepository.findAllByRequesterId(userId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            List<GetItemRequestDto> requests = itemRequestRepository.findAllByRequesterId(userId,
                            Sort.by(Sort.Direction.ASC, "created"))
                    .stream()
                    .map(ItemRequestMapper::toGetItemRequestDto)
                    .collect(Collectors.toList());

            for (GetItemRequestDto request : requests) {
                List<ItemDto> itemsByRequest = items
                        .stream()
                        .filter(l -> l.getRequestId() == request.getId())
                        .collect(Collectors.toList());
                request.setItems(itemsByRequest);
            }
            return requests;
        } else {
            throw new NotFoundException("Не удалось вернуть ответы");
        }
    }

    @Override
    public List<GetItemRequestDto> getRequestsPageable(long userId, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            List<GetItemRequestDto> requests = itemRequestRepository.findAllByRequesterIdIsNot(userId,
                            PageRequest.of((from / size), size))
                    .stream()
                    .map(ItemRequestMapper::toGetItemRequestDto)
                    .collect(Collectors.toList());
            List<ItemDto> items = itemRepository.findAllByRequesterIdIsNot(userId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());

            for (GetItemRequestDto request : requests) {
                List<ItemDto> itemsByRequest = items
                        .stream()
                        .filter(l -> l.getRequestId() == request.getId())
                        .collect(Collectors.toList());
                request.setItems(itemsByRequest);
            }
            return requests;
        } else {
            throw new NotFoundException("Не удалось вернуть ответы");
        }
    }

    @Override
    public GetItemRequestDto getRequestById(long userId, long requestId) {
        if (userRepository.existsById(userId) && itemRequestRepository.existsById(requestId)) {
            GetItemRequestDto requestDto = ItemRequestMapper.toGetItemRequestDto(
                    itemRequestRepository.findById(requestId).get());
            List<ItemDto> items = itemRepository.findAllByRequestId(requestId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            requestDto.setItems(items);

            return requestDto;
        } else {
            throw new NotFoundException("Данный ответ не существует");
        }
    }
}