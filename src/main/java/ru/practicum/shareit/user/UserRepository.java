package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for basic user repository operations.
 * It allows for retrieving User entities by ID, fetching all Users,
 * and checking for existence by email and ID.
 */
@Repository
public interface UserRepository {
    /**
     * Adds a new User entity to the repository.
     *
     * @param user the User entity to be added
     * @return the added User entity with a generated ID
     */
    User addUser(User user);

    /**
     * Updates an existing User entity with new values.
     *
     * @param id   the ID of the User entity to be updated
     * @param user the User entity with updated values
     * @return the updated User entity
     */
    Optional<User> updateUser(final Long id, User user);

    /**
     * Deletes a User entity from the repository based on the ID.
     *
     * @param id the ID of the User entity to be deleted
     */
    void deleteUser(final Long id);

    /**
     * Deletes a User entity from the repository based on the ID.
     *
     * @param email the email of the User entity to be deleted
     */
    void deleteEmail(String email);

    /**
     * Retrieves a User entity by its ID.
     *
     * @param id the ID of the User entity to retrieve
     * @return the User entity if found, or null otherwise
     */
    Optional<User> getById(final Long id);

    /**
     * Retrieves all User entities from the repository.
     *
     * @return a list of User entities
     */
    List<User> getAll();

    /**
     * Checks if a User entity exists in the repository with the provided email.
     *
     * @param email the email to check for existence
     * @return true if an entity with the email exists, false otherwise
     */
    Long existsByEmail(final String email);

    /**
     * Checks if a User entity exists in the repository with the provided ID.
     *
     * @param id the ID to check for existence
     * @return true if an entity with the ID exists, false otherwise
     */
    boolean existsById(final Long id);
}
