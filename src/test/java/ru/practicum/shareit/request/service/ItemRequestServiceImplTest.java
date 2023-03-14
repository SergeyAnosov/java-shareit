package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mappers.ItemRequestWithItemsMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private   ItemRequestServiceImpl itemRequestService;

    long userId = 1L;
    long requestId = 2L;
    User expectedUser = new User(1L, "test", "email");
    ItemRequest expectedRequest = new ItemRequest(2L, "description", expectedUser, LocalDateTime.of(2023, 12, 12, 12, 12));
    ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "description", null, LocalDateTime.of(2023, 12, 12, 12, 12));

    @Test
    void saveUser_whenUserExist_andSave() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));

        ItemRequestDto save = itemRequestService.save(itemRequestDto, userId);

        assertEquals(itemRequestDto.getId(), save.getId());
        assertEquals(itemRequestDto.getDescription(), save.getDescription());
        assertEquals(LocalDateTime.now().getYear(), save.getCreated().getYear());
        assertEquals(LocalDateTime.now().getMonth(), save.getCreated().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), save.getCreated().getDayOfMonth());

        verify(userRepository).findById(Mockito.anyLong());
        verify(itemRequestRepository).save(Mockito.any());
    }

    @Test
    void saveUser_whenUserNotFound_angExceptionThrown() {
        doThrow(NotFoundException.class).when(userRepository).findById(Mockito.anyLong());

        assertThrows(NotFoundException.class, () -> itemRequestService.save(itemRequestDto, userId));
        verify(itemRequestRepository, never()).save(expectedRequest);
    }


    @Test
    void getAllRequests() {
    }

    @Test
    void findAllRequestsByNotId() {
    }

    @Test
    void findById_whenUserFound_whenRequestFound_thenReturnItemRequestDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

        ItemRequestWithItemsDto byId = itemRequestService.findById(userId, requestId);
        byId.setItems(null);

        assertEquals(ItemRequestWithItemsMapper.INSTANCE.toDto(expectedRequest), byId);
    }

    @Test
    void findById_whenUserNotFound_thenReturnNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));
    }

    @Test
    void findById_whenUserFound_whenRequestNotFound_thenReturnNotFoundExceptionThrown() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));
    }
}