package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(header) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Create");
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@RequestHeader(header) Long userId,
                        @PathVariable long itemId) {
        log.debug("Read({})", itemId);
        return itemService.read(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> readAll(@RequestHeader(header) Long userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.debug("ReadAll");
        return itemService.readAll(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(header) Long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Update({})", itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(header) Long userId,
                       @PathVariable long itemId) {
        log.debug("Delete({})", itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(header) Long userId,
                                      @RequestParam String text,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        log.debug("Search({})", text);
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(header) Long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.debug("{}/CreateComment()", itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }
}

