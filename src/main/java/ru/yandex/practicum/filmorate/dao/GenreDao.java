package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    List<Genre> getFilmsGenre();

    Optional<Genre> getFilmGenreById(String id);

    List<Genre> getFilmGenreByFilmId(String id);
}
