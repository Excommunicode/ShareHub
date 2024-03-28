package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Stream;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Map<Long, User> USERS = new HashMap<>();
    private static final Map<String, Long> EMAILS = new HashMap<>();
    private Long id = 0L;


    @Override
    public User addUser(User user) {
        user.setId(++id);
        EMAILS.put(user.getEmail(), user.getId());
        USERS.put(user.getId(), user);
        return user;
    }


    @Override
    public Optional<User> updateUser(final Long id, User user) {
        checkDuplicateEmail(user);
        return Stream.of(user)
                .filter(x -> USERS.containsKey(x.getId()))
                .peek(x -> USERS.put(x.getId(), x))
                .findFirst();
    }


    @Override
    public void deleteUser(final Long id) {
        User user = USERS.remove(id);
        deleteEmail(user.getEmail());
    }


    @Override
    public Optional<User> getById(final Long id) {
        return Optional.of(USERS.get(id));
    }


    @Override
    public List<User> getAll() {
        return new ArrayList<>(USERS.values());
    }


    @Override
    public Long existsByEmail(final String email) {
        return EMAILS.get(email);
    }

    @Override
    public boolean existsById(final Long id) {
        for (Long aLong : USERS.keySet()) {
            if (aLong.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDuplicateEmail(User user) {
        return EMAILS.containsValue(user.getEmail());
    }

    @Override
    public void deleteEmail(final String email) {
        EMAILS.remove(email);
    }
}
