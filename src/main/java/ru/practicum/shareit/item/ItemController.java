package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemConstant.X_SHARER_USER_ID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createItem(@RequestHeader(X_SHARER_USER_ID) final Long userId,
                              @Valid @RequestBody final ItemDTO itemDTO) {
        return itemService.addItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDTO updateItem(@RequestHeader(X_SHARER_USER_ID) final Long userId,
                              @PathVariable final Long itemId,
                              @Valid @RequestBody final ItemDTO itemDTO) {
        return itemService.updateItem(userId, itemId, itemDTO);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getByID(@PathVariable final Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDTO> getAllItem(@RequestHeader(X_SHARER_USER_ID) final Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchByText(@RequestParam final String text) {
        return itemService.getItemsByNameOrDescription(text);
    }
}