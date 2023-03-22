package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    private User user;
    private Item item;
    private Booking booking;
    private Comment comment;
    private final NotFoundException notFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "сущность не найдена");


    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Имя", "Мыло");
        item = new Item(1L, "вещь", "описание", true, user, 1L);
        booking = new Booking(1L,
                LocalDateTime.of(2025, 1,1,1,15, 10),
                LocalDateTime.of(2025, 2,1,1,15, 10),
                item,
                user,
                BookingStatus.APPROVED);
        comment = new Comment(1L, "это комментарий", item, user, LocalDateTime.now());

    }

    @Test
    void save_success() {
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto saveItemDto = itemService.save(ItemMapper.toItemDto(item), 1L, UserMapper.toUserDto(user));

        Assertions.assertAll(
                () -> Assertions.assertEquals(item.getId(), saveItemDto.getId()),
                () -> Assertions.assertEquals(item.getName(), saveItemDto.getName()),
                () -> Assertions.assertEquals(item.getDescription(), saveItemDto.getDescription()),
                () -> Assertions.assertEquals(item.getAvailable(), saveItemDto.getAvailable()),
                () -> Assertions.assertEquals(item.getRequestId(), saveItemDto.getRequestId())
        );
    }

    @Test
    void save_userNotFound() {
        when(userRepository.saveAndFlush(any())).thenThrow(notFoundException);

        assertThrows(NotFoundException.class, () -> itemService.save(ItemMapper.toItemDto(item), 1L, UserMapper.toUserDto(user)));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_Success_andUpdateAllFields() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item newItem = new Item(1L, "новое название",
                "новое описание", false, user, 1L);
        ItemDto toUpdate = ItemMapper.toItemDto(newItem);

       itemService.update(toUpdate, 1L, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("новое название", savedItem.getName());
        assertEquals("новое описание", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateItem_Fail_whenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenThrow(notFoundException);
        Item newItem = new Item(1L, "новое название",
                "новое описание", false, user, 1L);
        ItemDto toUpdate = ItemMapper.toItemDto(newItem);

        assertThrows(NotFoundException.class, () ->  itemService.update(toUpdate, 1L, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_Fail_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(notFoundException);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Item newItem = new Item(1L, "новое название",
                "новое описание", false, user, 1L);
        ItemDto toUpdate = ItemMapper.toItemDto(newItem);

        assertThrows(NotFoundException.class, () ->  itemService.update(toUpdate, 1L, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_Success_BlankFieldName() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item newItem = new Item(1L, "",
                "новое описание", false, user, 1L);
        ItemDto toUpdate = ItemMapper.toItemDto(newItem);

        itemService.update(toUpdate, 1L, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("вещь", savedItem.getName());
        assertEquals("новое описание", savedItem.getDescription());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void updateItem_Succes_BlankFieldDescriptionAndNullAvailable() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item newItem1 = new Item(1L, "afdsgsg",
                "", null, user, 1L);
        ItemDto toUpdate1 = ItemMapper.toItemDto(newItem1);

        itemService.update(toUpdate1, 1L, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem1 = itemArgumentCaptor.getValue();
        assertEquals("afdsgsg", savedItem1.getName());
        assertEquals("описание", savedItem1.getDescription());
        assertEquals(true, savedItem1.getAvailable());
    }


    @Test
    void getById_Success_CheckGetOwnerSuccess() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem_Id(anyLong())).thenReturn(List.of(comment));
        when(bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(booking);
        when(bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(anyLong(), any(), any())).thenReturn(booking);

        ItemInfoDto itemDto = itemService.getById(1L, 1L);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());

        assertEquals(booking.getId(), itemDto.getNextBooking().getId());
        assertEquals(booking.getStart(), itemDto.getNextBooking().getStart());
        assertEquals(booking.getStart(), itemDto.getNextBooking().getStart());
        assertEquals(booking.getEnd(), itemDto.getNextBooking().getEnd());
        assertEquals(booking.getStatus(), itemDto.getNextBooking().getStatus());

        assertEquals(booking.getId(), itemDto.getLastBooking().getId());
        assertEquals(booking.getStart(), itemDto.getLastBooking().getStart());
        assertEquals(booking.getStart(), itemDto.getLastBooking().getStart());
        assertEquals(booking.getEnd(), itemDto.getLastBooking().getEnd());
        assertEquals(booking.getStatus(), itemDto.getLastBooking().getStatus());

        CommentDto comment1 = itemDto.getComments().get(0);

        assertEquals(itemDto.getComments().size(), 1);
        assertEquals(comment1.getId(), comment.getId());
        assertEquals(comment1.getAuthorName(), comment.getAuthor().getName());
        assertEquals(comment1.getText(), comment.getText());
    }

    @Test
    void getById_Fail_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void search() {
    }

    @Test
    void createComment() {
    }
}