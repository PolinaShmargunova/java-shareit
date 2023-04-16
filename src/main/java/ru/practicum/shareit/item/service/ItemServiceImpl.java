package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto dto, long ownerId) throws NotFoundException {
        log.info("Добавлен предмет");
        return itemStorage.addItem(dto,userStorage.get(ownerId));
    }

    @Override
    public ItemDto patchItem(ItemDto dto, long ownerId, long itemId) throws NotFoundException {
        User owner = userStorage.get(ownerId);
        log.info("Обновлен предмет с id " + itemId);
        return itemStorage.patchItem(dto,owner,itemId);
    }

    @Override
    public ItemDto getItem(long itemId, long ownerId) {
        log.info("Получен предмет с id " + itemId);
        return itemStorage.getItem(itemId,ownerId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long ownerId) {
        return itemStorage.getAllItemsByOwner(ownerId);
    }

    @Override
    public List<ItemDto> searchItem(String text, long ownerId) {
        return itemStorage.searchItem(text,ownerId);
    }
}
