package ru.practicum.shareitserver.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.CommentDtoShort;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemInfoDto;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.service.UserService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        UserDto userDto = userService.getById(userId);
        return itemService.save(itemDto, userId, userDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemInfoDto getById(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("вызов вещи по индексу {}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(name = "size",required = false, defaultValue = "10") int size) {
        return itemService.getOwnerItems(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestParam String text,
                                     @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                     @RequestParam(name = "size",required = false, defaultValue = "10") int size) {
        if (!text.isBlank()) {
            return itemService.search(PageRequest.of(from,size), text);
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDtoShort commentDtoShort, @RequestHeader("X-Sharer-User-Id") long authorId,
                                    @PathVariable long itemId) {
        return itemService.createComment(commentDtoShort, authorId, itemId);
    }
}
