package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto save(ItemDto itemDto, Long userId, UserDto userDto) {
        Item item = ItemMapper.toItem(itemDto, userDto.getId());
        User user = UserMapper.toUser(userDto);
        userRepository.saveAndFlush(user);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        checkItem(itemId);
        checkUserId(itemId, userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Предмет с таким id не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "такого юзера нет"));

        if ((itemDto.getName() != null) && (!itemDto.getName().isBlank())) {
            item.setName(itemDto.getName());
        }
        if ((itemDto.getDescription() != null) && (!itemDto.getDescription().isBlank())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemInfoDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Предмет с таким id не найден"));
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item);
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(itemId, itemInfoDto);
        }
        List<CommentDto> comments = commentRepository.findAllByItem_Id(itemId).stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemInfoDto.setComments(comments);
        return itemInfoDto;
    }

    @Override
    public List<ItemInfoDto> getOwnerItems(int from, int size, Long userId) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<ItemInfoDto> itemsInfoDto = itemRepository.getOwnersItems(userId, pageRequest)
                .getContent().stream()
                .map(ItemMapper::toItemInfoDto).collect(Collectors.toList());
        for (ItemInfoDto itemInfoDto : itemsInfoDto) {
            setLastAndNextBooking(itemInfoDto.getId(), itemInfoDto);
        }
        return itemsInfoDto;
    }

    @Override
    public List<ItemDto> search(PageRequest pageRequest, String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.search(text, pageRequest).getContent()) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDtoShort commentDtoShort, Long authorId, Long itemId) {
        User user = userRepository.findById(authorId).orElseThrow(() -> new WrongUserException("такого юзера нет"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("такой вещи нет"));
        checkUserTookItem(authorId, itemId);
        Comment comment = CommentMapper.toComment(commentDtoShort);
        comment.setItem(item);
        comment.setAuthor(user);
        log.debug("Сохраняем комментарий для вещи" + itemId + "от пользователя " + authorId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void checkUserTookItem(Long userId, Long itemId) {
        if (bookingRepository.findAllByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            log.info("проверка что пользователь брал данную вещь не прошла");
            throw new BadRequestException("данный юзер не брал эту вещь");
        }
    }

    private void checkItem(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Предмет с таким id не найден");
        }
    }

    private void checkUserId(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isPresent()) {
            if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
                throw new NotFoundException(HttpStatus.NOT_FOUND, "Редактировать вещь может только её владелец");
            }
        }
    }

    private void setLastAndNextBooking(Long itemId, ItemInfoDto itemInfoDto) {
        Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (lastBooking != null) {
            itemInfoDto.setLastBooking(BookingMappers.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemInfoDto.setNextBooking(BookingMappers.toBookingDto(nextBooking));
        }
    }
}
