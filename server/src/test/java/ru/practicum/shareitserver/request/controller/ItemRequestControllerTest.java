package ru.practicum.shareitserver.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareitserver.request.ItemRequestController;
import ru.practicum.shareitserver.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareitserver.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void getAllRequests_whenInvocked_thenReturnItemReqDtoCollection() {
        List<ItemRequestWithItemsDto> expectedItemReqDto = List.of(new ItemRequestWithItemsDto());
        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong()))
                .thenReturn(expectedItemReqDto);

        List<ItemRequestWithItemsDto> response = itemRequestController.getAllRequests(5L);

        assertEquals(expectedItemReqDto, response);
    }


    @Test
    void create() {
    }

    @Test
    void findRequestsWithFromAndSize() {
    }

    @Test
    void findById() {
    }
}