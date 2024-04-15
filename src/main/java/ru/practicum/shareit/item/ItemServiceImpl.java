package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapperShortDTO;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShortDTO;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static ru.practicum.shareit.booking.BookingState.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = REPEATABLE_READ)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService, CommentService {
    ItemRepository itemRepository;
    ItemMapper itemMapper;
    UserRepository userRepository;
    UserMapper userMapper;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    BookingMapperShortDTO bookingMapperShortDTO;

    @Transactional
    @Override
    public ItemDTO addItem(final Long userId, ItemDTO itemDTO) {
        log.debug("Starting addItem operation for user ID: {}", userId);

        if (isItemDataInvalid(itemDTO)) {
            log.warn("Attempt to add item with invalid data by user ID: {}", userId);
            throw new BadRequestException("Provided item data is invalid.");
        }

        verifyUserExists(userId);
        itemDTO.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
        Item savedItem = itemRepository.save(itemMapper.toModel(itemDTO));
        log.info("New item added with ID: {}", savedItem.getId());

        return itemMapper.toDTO(savedItem);
    }

    @Transactional
    @Override
    public ItemDTO updateItem(final Long userId, final Long itemId, ItemDTO itemDTO) {
        log.debug("Starting updateItem operation for item ID: {} by user ID: {}", itemId, userId);

        verifyUserExists(userId);
        Item item = getItemById(itemId);
        verifyOwnership(userId, item);

        mapItemDetails(item, itemDTO);
        Item updatedItem = itemRepository.save(item);
        log.info("Item ID: {} updated by user ID: {}", itemId, userId);

        return itemMapper.toDTO(updatedItem);
    }

    @Override
    public ItemDTO findItemById(final Long itemId, final Long userId) {
        log.debug("Attempting to find item by ID: {} for user ID: {}", itemId, userId);

        final LocalDateTime now = LocalDateTime.now();
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        log.debug("Item found with ID: {}. Proceeding with data assembly.", itemId);

        final ItemDTO responseItem = itemMapper.toDTO(item);
        final List<CommentDTO> comments = commentMapper.toDTOList(commentRepository.findByItem_IdOrderByCreatedDesc(itemId));
        log.debug("Number of comments loaded: {}", comments.size());

        if (!responseItem.getOwner().getId().equals(userId)) {
            responseItem.setComments(comments);
            log.debug("User ID: {} is not the item owner. Only comments set.", userId);
        } else {
            BookingShortDTO lastBooking = bookingMapperShortDTO.toDTO(
                    bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusIsNotOrderByStartDesc(itemId, now, REJECTED)
                            .orElse(null));
            BookingShortDTO nextBooking = bookingMapperShortDTO.toDTO(
                    bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusIsNotOrderByStartAsc(itemId, now, REJECTED)
                            .orElse(null));

            responseItem.setLastBooking(lastBooking);
            responseItem.setNextBooking(nextBooking);
            responseItem.setComments(comments);
            log.debug("Full item details set for the owner with user ID: {}", userId);
        }

        log.info("Item retrieval complete for item ID: {} and user ID: {}", itemId, userId);
        return responseItem;
    }


    @Override
    public List<ItemDTO> getItems(final Long userId) {
        log.debug("Retrieving items for user ID: {}", userId);

        final LocalDateTime now = LocalDateTime.now();
        final List<ItemDTO> items = itemMapper.toListDTO(itemRepository.findAllByOwnerIdOrderById(userId));

        items.forEach(item -> {
            item.setLastBooking(bookingMapperShortDTO.toDTO(bookingRepository
                    .findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(item.getId(), now)
                    .orElse(null)));
            item.setNextBooking(bookingMapperShortDTO.toDTO(bookingRepository
                    .findFirstByItem_IdAndStartIsAfterOrderByStartAsc(item.getId(), now)
                    .orElse(null)));
        });
        return items;
    }

    @Override
    public List<ItemDTO> getItemsByNameOrDescription(final String text) {
        log.debug("Searching for items by text: '{}'", text);

        if (text == null || text.trim().isEmpty()) {
            log.info("Search text is empty or null. Returning empty list.");
            return new ArrayList<>();
        }

        final List<Item> items = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text);
        return itemMapper.toListDTO(items);
    }

    @Transactional
    @Override
    public CommentDTO addComment(Long userId, Long itemId, CommentDTO commentDTO) {
        log.info("Adding a comment with id: {}", userId);

        if (commentDTO.getText() == null || commentDTO.getText().trim().isEmpty()) {
            throw new BadRequestException("Comment text cannot be empty");
        }

        UserDTO user = userMapper.toDTO(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)));
        ItemDTO item = itemMapper.toDTO(getItemById(itemId));

        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusInAndEndBefore(userId, itemId,
                List.of(APPROVED, CANCELED), LocalDateTime.now())) {
            throw new BadRequestException("Booking already in progress");
        }

        Comment savedComment = commentMapper.toModel(commentDTO).toBuilder()
                .author(userMapper.toModel(user))
                .created(LocalDateTime.now())
                .item(itemMapper.toModel(item))
                .build();

        CommentDTO savedCommentDTO = commentMapper.toDTO(commentRepository.save(savedComment));
        log.info("New comment added with ID: {}", savedCommentDTO.getId());
        return savedCommentDTO;
    }

    private void verifyUserExists(final Long userId) {
        log.debug("Verifying existence of user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new NotFoundException("User does not exist.");
        }
    }

    private boolean isItemDataInvalid(final ItemDTO itemDTO) {
        boolean invalid = itemDTO.getName() == null || itemDTO.getName().trim().isEmpty() ||
                itemDTO.getDescription() == null || itemDTO.getAvailable() == null;
        log.debug("Item data validation result: {}", invalid ? "Invalid" : "Valid");
        return invalid;
    }

    private Item getItemById(final Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Item not found with ID: {}", itemId);
            return new NotFoundException("Item not found.");
        });
    }

    private void verifyOwnership(final Long userId, final Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("User ID: {} does not own the item ID: {}", userId, item.getId());
            throw new NotFoundException("User is not the owner of the item.");
        }
    }

    private void mapItemDetails(final Item item, final ItemDTO itemDTO) {
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
    }
}

