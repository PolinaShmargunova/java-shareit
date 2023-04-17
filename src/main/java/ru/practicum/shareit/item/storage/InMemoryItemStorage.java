package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Component
public class InMemoryItemStorage implements ItemStorage {

    Map<Long, Item> items = new HashMap<>();
    long id = 1;

    @Override
    public ItemDto addItem(ItemDto dto, User owner) {
        dto.setId(id++);
        items.put(dto.getId(), toItem(dto,owner));

        return toItemDto(items.get(dto.getId()));
    }

    @Override
    public ItemDto patchItem(ItemDto dto, User owner, long itemId) throws NotFoundException {
        if (getItemOwnerId(itemId) != owner.getId()) {
            throw new NotFoundException("Эта вещь принадлежит другому пользователю, вы не можете обновить данные");
        }
        Item oldItem = items.get(itemId);
        if (dto.getName() == null) {
            dto.setName(oldItem.getName());
        }
        if (dto.getDescription() == null) {
            dto.setDescription(oldItem.getDescription());
        }
        if (dto.getAvailable() == null) {
            dto.setAvailable(oldItem.isAvailable());
        }
        dto.setId(itemId);
        items.put(itemId,toItem(dto, owner));

        return toItemDto(items.get(itemId));
    }

    public long getItemOwnerId(long itemId) throws NotFoundException {
        if (itemId == 0) {
            throw new NotFoundException("Данная вещь не найдена");
        }
        return items.get(itemId).getOwner().getId();
    }

    @Override
    public ItemDto getItem(long itemId, long ownerId) {
        return toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long ownerId) {
        List<ItemDto> allItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == ownerId) {
                allItems.add(toItemDto(item));
            }
        }
        return allItems;
    }

    @Override
    public List<ItemDto> searchItem(String text, long ownerId) {
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.isAvailable() && !text.equals("")) {
                if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) {
                    foundItems.add(toItemDto(item));
                }
            }
        }
        return foundItems;
    }
}
