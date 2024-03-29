package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody final UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@Valid @PathVariable final Long userId, @RequestBody final UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }


    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable final Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        userService.deleteUser(userId);
    }
}
