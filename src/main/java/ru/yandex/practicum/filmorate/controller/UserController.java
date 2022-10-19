package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("Текущее количество пользователей: " + userService.getInMemoryUserStorage().getAll().size());
        return userService.getInMemoryUserStorage().getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable final int id) {
        if (id > userService.getInMemoryUserStorage().getAll().size()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного пользователя не существует");
        } else {
            return userService.getInMemoryUserStorage().getAll().get(id-1);
        }
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable final int id) {
        if (id > userService.getInMemoryUserStorage().getAll().size()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного пользователя не существует");
        } else {
            List<User> friendsList = new ArrayList<>();
            for (Integer friendId: userService.getInMemoryUserStorage().getAll().get(id-1).getFriendsList()){
                friendsList.add(userService.getInMemoryUserStorage().getAll().get(friendId-1));
            }
            return friendsList;
        }
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable final int id, @PathVariable final int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        return userService.getInMemoryUserStorage().create(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException {
        return userService.getInMemoryUserStorage().update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public int addFriend(@PathVariable final int id, @PathVariable final int friendId) {
        if (friendId < 0 || friendId > userService.getInMemoryUserStorage().getAll().size()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного друга не существует");
        } else {
            log.debug("Пользователю c Id: {}, добавлен друг с Id: {}", id, friendId);
            return userService.addFriend(id, friendId);
        }
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public int deleteFriend(@PathVariable final int id, @PathVariable final int friendId) {
        log.debug("У пользователя c Id: {}, удален друг с Id: {}", id, friendId);
        return userService.removeFriend(id, friendId);
    }
}
