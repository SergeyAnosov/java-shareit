package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mappers.ItemRequestWithItemsMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
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
    @Mock
    private UserService userService;
    @InjectMocks
    private   ItemRequestServiceImpl itemRequestService;
    long userId = 1L;
    long requestId = 2L;
    private User expectedUser = new User(1L, "test", "email");
    private User user2 = new User(2L, "name2", "email2");
    private ItemRequest expectedRequest = new ItemRequest(2L, "description", expectedUser, LocalDateTime.of(2023, 12, 12, 12, 12));
    private ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "description", null, LocalDateTime.of(2023, 12, 12, 12, 12));
    private NotFoundException notFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "не найдено");

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
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(Mockito.anyLong())).thenReturn(List.of(expectedRequest));

        assertEquals(1, itemRequestService.getAllRequests(1L).size());
    }

    @Test
    void getAllRequests_FAil_whenUserNotFound() {
        when(userRepository.findById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(1L));
    }

    @Test
    void findAllRequestsByNotId() {
        ItemRequest itemRequest2 = new ItemRequest(
                2L,
                "Описание2",
                user2,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

       // Item newItem = new Item(2L, "Название", "Описание", true, expectedUser, 2L);
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(any(), any())).thenReturn(new PageImpl<>(List.of(itemRequest2)));

        assertEquals(1, itemRequestService.findAllRequestsByNotId(PageRequest.of(0, 10), 1L).size());
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