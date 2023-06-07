package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping()
    public ItemRequestDto create(@RequestHeader(header) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Create");
        return requestService.create(userId, itemRequestDto);
    }

    @GetMapping()
    public Collection<ItemRequestDto> read(@RequestHeader(header) Long userId) {
        log.debug("Read");
        return requestService.read(userId);
    }

    @GetMapping("{itemId}")
    public ItemRequestDto read(@RequestHeader(header) Long userId,
                               @PathVariable Long itemId) {
        log.debug("Read({})", itemId);
        return requestService.read(userId, itemId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> readAll(@RequestHeader(header) Long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.debug("ReadAll");
        return requestService.readAll(userId, from, size);
    }
}
