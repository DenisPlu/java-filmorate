package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();
    Optional<User> create(User user) throws ValidationException;
    Optional<User> update(User user) throws ValidationException;
    Optional<User> findUserById(String id);
}
