package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * The UserController class handles HTTP requests related to user operations.
 * It serves as a RESTful API controller for managing users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userDTO the user to be created
     * @return the created user
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    /**
     * Updates an existing user's information.
     *
     * @param userId  the ID of the user to be updated
     * @param userDTO the updated user information
     * @return the updated user
     */
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable Long userId,
                              @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the retrieved user, represented as a {@link UserDTO}
     */
    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    /**
     * Retrieves a list of all users in the application.
     *
     * @return a list of all users, each represented as a {@link UserDTO}
     */
    @GetMapping
    public List<UserDTO> getAll(@RequestParam Integer from,
                                @RequestParam Integer size) {
        return userService.getAll(from, size);
    }
}