package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDTO itemDTO) {
        return itemService.addItem(userId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable(value = "itemId") Long itemId,
                              @Valid @RequestBody ItemDTO itemDTO) {
        log.info("запрос получен");
        return itemService.updateItem(userId, itemId, itemDTO);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getByID(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDTO> getAllItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> searchByText(@RequestParam String text) {
        log.info("текст получен");
        return itemService.getItemsByNameOrDescription( text);
    }

}
