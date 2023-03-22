package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        UserDto userDto = userService.getById(userId);
        return itemService.save(itemDto, userId, userDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @Positive @RequestParam(name = "size",required = false, defaultValue = "10") int size) {
        return itemService.getOwnerItems(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                     @Positive @RequestParam(name = "size",required = false, defaultValue = "10") int size) {
        if (!text.isBlank()) {
            return itemService.search(PageRequest.of(from,size), text);
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDtoShort commentDtoShort, @RequestHeader("X-Sharer-User-Id") long authorId,
                                    @PathVariable long itemId) {
        return itemService.createComment(commentDtoShort, authorId, itemId);
    }
}
