package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserManualValidator;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final List<User> users = new ArrayList<>();
    private Integer currentId = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public List<User> getAll(){
        return users;
    }

    @Override
    public User create(User user) throws ValidationException {
        if (UserManualValidator.isValid(user)) {
            log.info("Добавлен пользователь: {}", user);
            user.setId(currentId);
            users.add(user);
            this.currentId++;
            return user;
        } else {
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.");
        }
    }

    @Override
    public User update(User user) throws ValidationException {
        if (user.getId() <= 0) {
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: id.");
        } else if ((user.getId() - 1) > users.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного для обновления пользователя не существует");
        } else if (UserManualValidator.isValid(user)) {
            log.info("Обновлен пользователь: {}", user);
            users.set((user.getId() - 1), user);
            return user;
        } else {
            log.debug("Ошибка данных. Email: {}, Login: {}, Birthday: {}", user.getEmail(), user.getLogin(), user.getBirthday());
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.");
        }
    }
}
