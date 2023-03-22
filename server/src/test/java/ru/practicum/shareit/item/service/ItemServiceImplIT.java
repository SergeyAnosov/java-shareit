package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@Transactional
@Rollback(false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplIT {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;
    @MockBean
    private BookingRepository bookingRepository;
    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @Test
    @Order(1)
    void save_update_getById_createComment() {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("Email@mail.ru");

        itemRequest = new ItemRequest(1L, "описание", user,
                LocalDateTime.of(2023, 12, 12, 12, 12, 20));

        item = new Item();
        item.setId(1L);
        item.setName("вещь");
        item.setDescription("описание");
        item.setAvailable(true);
        item.setRequestId(1L);

        UserDto userDto = userService.save(UserMapper.toUserDto(user));
        itemRequestService.save(ItemRequestMapper.INSTANCE.toDto(itemRequest), user.getId());
        ItemDto itemDto = itemService.save(ItemMapper.toItemDto(item), userDto.getId(), userDto);

        assertEquals(itemDto.getId(), itemDto.getId());
        assertEquals(itemDto.getName(), itemDto.getName());
        assertEquals(itemDto.getDescription(), itemDto.getDescription());
        assertEquals(itemDto.getRequestId(), itemDto.getRequestId());

        ItemDto itemDtoBlankName = new ItemDto();
        itemDtoBlankName.setName("");
        itemDtoBlankName.setDescription("updateDescr");

        itemService.update(itemDtoBlankName, 1L, 1L);
        ItemInfoDto update1 = itemService.getById(1L, 1L);

        assertEquals(update1.getName(), item.getName());
        assertEquals(update1.getDescription(), "updateDescr");

        ItemDto itemDtoBlankDescription = new ItemDto();
        itemDtoBlankDescription.setName("newName");
        itemDtoBlankDescription.setDescription("");

        itemService.update(itemDtoBlankDescription, 1L, 1L);
        ItemInfoDto update2 = itemService.getById(1L, 1L);

        assertEquals(update2.getName(), "newName");
        assertEquals(update2.getDescription(), "updateDescr");

        ItemDto itemDtoCorrect = new ItemDto();
        itemDtoCorrect.setName("correctName");
        itemDtoCorrect.setDescription("correctDescription");

        itemService.update(itemDtoCorrect, 1L, 1L);

        ItemInfoDto update3 = itemService.getById(1L, 1L);

        assertEquals("correctName", update3.getName());
        assertEquals("correctDescription", update3.getDescription());

        List<ItemInfoDto> ownerItems = itemService.getOwnerItems(0, 10, 1L);

        assertEquals(1, ownerItems.size());
        assertEquals(1L, ownerItems.get(0).getId());
        assertEquals("correctName", ownerItems.get(0).getName());

        List<ItemDto> searchItem = itemService.search(PageRequest.of(0, 10), "correct");

        assertEquals(1, ownerItems.size());
        assertEquals(1L, ownerItems.get(0).getId());
        assertEquals("correctName", ownerItems.get(0).getName());

        CommentDtoShort commentDtoShort = new CommentDtoShort(1L, "первый комментарий");

        Mockito.when(bookingRepository.findAllByBooker_IdAndItem_IdAndEndIsBefore(anyLong(),anyLong(), any()))
                .thenReturn(List.of(new Booking()));

        CommentDto comment = itemService.createComment(commentDtoShort, 1L, 1L);

        assertEquals("Ivan", comment.getAuthorName());
        assertEquals("correctDescription", comment.getItem().getDescription());
    }
}