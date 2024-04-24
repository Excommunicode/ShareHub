package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utils.Constant.X_SHARER_USER_ID;
import static ru.practicum.shareit.utils.Marker.OnCreate;
import static ru.practicum.shareit.utils.Marker.OnUpdate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;
    CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createItem(
            @RequestHeader(X_SHARER_USER_ID) final Long userId,
            @Validated(OnCreate.class) @RequestBody final ItemDTO itemDTO) {
        return itemService.addItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDTO updateItem(
            @RequestHeader(X_SHARER_USER_ID) final Long userId,
            @PathVariable final Long itemId,
            @Validated(OnUpdate.class) @RequestBody final ItemDTO itemDTO) {
        return itemService.updateItem(userId, itemId, itemDTO);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getById(@PathVariable final Long itemId, @RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDTO> getAllItem(@RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchByText(@RequestParam final String text) {
        return itemService.getItemsByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(
            @RequestHeader(X_SHARER_USER_ID) final Long userId,
            @PathVariable final Long itemId,
            @Valid @RequestBody final CommentDTO commentDTO) {
        return commentService.addComment(userId, itemId, commentDTO);
    }
}