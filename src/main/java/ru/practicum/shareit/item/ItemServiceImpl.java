package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
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

        verifyUserExists(userId);

        itemDTO.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));

        ItemDTO savedItem = itemMapper.toDTO(itemRepository.save(itemMapper.toModel(itemDTO)));

        log.info("New item added with ID: {}", savedItem.getId());
        return savedItem;
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


        List<ItemDTO> items = itemMapper.toListDTO(itemRepository.findAllByOwnerIdOrderById(userId));

        Map<Long, List<Booking>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemMapper.toModelList(items), APPROVED)
                .stream()
                .collect(groupingBy(x -> x.getItem().getId(), toList()));

        List<ItemDTO> collect = items
                .stream()
                .map(itemDTO -> seBookings(itemDTO, bookings.get(itemDTO.getId())))
                .collect(toList());
        log.info("Number of items retrieved: {}", collect.size());
        return collect;
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
    public CommentDTO addComment(final Long userId, final Long itemId, CommentDTO commentDTO) {
        log.debug("Adding a comment with id: {}", userId);

        UserDTO user = userMapper.toDTO(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)));
        ItemDTO item = itemMapper.toDTO(getItemById(itemId));

        if (!bookingRepository.existsByBookerIdAndItem_IdAndStatusInAndEndBefore(userId, itemId,
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

    private ItemDTO seBookings(ItemDTO itemDTO, final List<Booking> bookings) {
        if (bookings == null) {
            return itemDTO;
        }

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = (bookings
                .stream()
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null));

        Booking nextBooking = bookings
                .stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);


        if (lastBooking != null) {
            itemDTO.setLastBooking(bookingMapperShortDTO.toDTO(lastBooking));
        }

        if (lastBooking != null) {
            itemDTO.setNextBooking(bookingMapperShortDTO.toDTO(nextBooking));
        }
        return itemDTO;
    }
}