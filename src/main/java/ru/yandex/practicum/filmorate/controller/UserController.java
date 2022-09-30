package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Контроллер для класса User, ручная валидация
@RestController
public class UserController {
    private final List<User> users = new ArrayList<>();
    int currentId = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> getAll() {
        log.debug("Текущее количество пользователей: " + users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        boolean isEmailEmpty = user.getEmail().equals("") || !user.getEmail().contains("@");
        boolean isLoginEmpty = user.getLogin().equals("") || user.getLogin().contains(" ");
        boolean isBirthdayCorrect = user.getBirthday().isAfter(LocalDate.now());
        Optional<String> optionalEmail = Optional.ofNullable(user.getName());
        if (optionalEmail.isEmpty() || optionalEmail.get().equals("")) {
            user.setName(user.getLogin());
        }
        if (!(isEmailEmpty || isLoginEmpty || isBirthdayCorrect)) {
            log.debug("Добавлен пользователь: {}", user);
            user.setId(currentId);
            users.add(user);
            this.currentId++;
            return user;
        } else {
            log.debug("Ошибка данных. Email: {}, Login: {}, Birthday: {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.");
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException {
        boolean isEmailEmpty = user.getEmail().equals("") || !user.getEmail().contains("@");
        boolean isLoginEmpty = user.getLogin().equals("") || user.getLogin().contains(" ");
        boolean isBirthdayCorrect = user.getBirthday().isAfter(LocalDate.now());
        Optional<String> opt = Optional.ofNullable(user.getName());
        if (opt.isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getId() <= 0) {
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: id.");
        } else if (!(isEmailEmpty || isLoginEmpty || isBirthdayCorrect) && users.get(user.getId() - 1) != null) {
            log.debug("Обновлен пользователь: {}", user);
            users.set(user.getId() - 1, user);
            return user;
        } else {
            log.debug("Ошибка данных. Email: {}, Login: {}, Birthday: {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.");
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
