package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> findFilmById(String id);

    String addLike(String filmId, String userId);

    List<Integer> getFilmsLikes(String id);

    int removeLike(int filmId, int userId);
}
