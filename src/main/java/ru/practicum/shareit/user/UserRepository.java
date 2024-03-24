package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static ru.practicum.shareit.constant.UserConstant.USER_REPOSITORY_IMPL;

@Repository(value = USER_REPOSITORY_IMPL)
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsById(Long id);
}