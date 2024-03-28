package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The UserService interface defines the operations that can be performed on users within the application.
 * It provides methods for creating, updating, retrieving, and deleting users, as well as listing all users
 * and checking if a user exists by their ID.
 */
@Service
public interface UserService {

    /**
     * Adds a new user to the application.
     *
     * @param userDTO the user to be added, represented as a {@link UserDTO}
     * @return the added user, represented as a {@link UserDTO} with an assigned ID
     */
    UserDTO addUser(UserDTO userDTO);

    /**
     * Updates an existing user's information.
     *
     * @param id   the ID of the user to be updated
     * @param user the updated user information, represented as a {@link UserDTO}
     * @return the updated user, represented as a {@link UserDTO}
     */
    UserDTO updateUser(final Long id, UserDTO user);

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the retrieved user, represented as a {@link UserDTO}
     */
    UserDTO getById(final Long id);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(final Long id);

    /**
     * Retrieves a list of all users in the application.
     *
     * @return a list of all users, each represented as a {@link UserDTO}
     */
    List<UserDTO> getAll();

    /**
     * Checks if a user exists in the application by their ID.
     *
     * @param id the ID of the user to check
     * @return true if the user exists, false otherwise
     */
    boolean isExistUser(final Long id);
}
