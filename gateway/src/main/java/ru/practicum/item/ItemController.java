package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(header) long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> read(@RequestHeader(header) long userId,
                                       @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.read(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> readAll(@RequestHeader(header) long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get items, userId={}", userId);
        return itemClient.readAll(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(header) long userId,
                                         @PathVariable long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Update item {}, userId={}", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader(header) long userId,
                                         @PathVariable long itemId) {
        log.info("Delete item {}, userId={}", itemId, userId);
        return itemClient.delete(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(header) long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Find items by text {}, userId={}", text, userId);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(header) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating comment {} for item {}, userId={}", commentDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
