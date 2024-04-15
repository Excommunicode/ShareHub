
package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
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
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable final Long userId, @RequestBody final UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }


}