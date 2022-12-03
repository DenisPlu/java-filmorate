package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);
}
