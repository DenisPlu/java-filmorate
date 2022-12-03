package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("Текущее количество пользователей: " + userService.getUserStorage().getAll().size());
        return userService.getUserStorage().getAll();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable final String id) {
        return userService.getUserStorage().findUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable final String id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable final String id, @PathVariable final String otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping(value = "/users")
    public Optional<User> create(@RequestBody User user) throws ValidationException {
        return userService.getUserStorage().create(user);
    }

    @PutMapping(value = "/users")
    public Optional<User> update(@RequestBody User user) throws ValidationException {
        return userService.getUserStorage().update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public String addFriend(@PathVariable final String id, @PathVariable final String friendId) {
        log.debug("Пользователю c Id: {}, добавлен друг с Id: {}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public int deleteFriend(@PathVariable final int id, @PathVariable final int friendId) {
        log.debug("У пользователя c Id: {}, удален друг с Id: {}", id, friendId);
        return userService.getUserStorage().getFriendshipStorage().removeFriend(id, friendId);
    }
}
