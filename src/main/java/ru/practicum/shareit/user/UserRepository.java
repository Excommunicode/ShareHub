package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static ru.practicum.shareit.user.UserConstant.USER_REPOSITORY_IMPL;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(final String email);

    boolean existsById(final Long id);
}