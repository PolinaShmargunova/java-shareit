package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
public class RequestControllerTest {
    @MockBean
    ItemRequestService requestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void addRequest() throws Exception {
        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, 0);

        when(requestService.addRequest(any(), anyLong()))
                .thenReturn(ItemRequestMapper.toItemRequest(requestDto, 1));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequests() throws Exception {
        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, 0);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, 1);
        when(requestService.getRequests(anyLong()))
                .thenReturn(List.of(ItemRequestMapper.toGetItemRequestDto(itemRequest)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsPageable() throws Exception {
        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, 0);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, 1);
        when(requestService.getRequestsPageable(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemRequestMapper.toGetItemRequestDto(itemRequest)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById() throws Exception {
        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("test");
        ItemDto itemDto = new ItemDto(1, "test", "testtest", true, 0);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, 1);
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(ItemRequestMapper.toGetItemRequestDto(itemRequest));

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}