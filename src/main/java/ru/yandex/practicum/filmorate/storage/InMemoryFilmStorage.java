package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private Integer currentId = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Override
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: " + films.size());
        return films;
    }

    @Override
    public Optional<Film> create(Film film) {
        film.setId(currentId);
        if (Optional.ofNullable(film.getLikes()).isPresent()) {
            film.setLikesNumber(film.getLikes().size());
        } else {
            film.setLikesNumber(0);
        }
        films.add(film);
        this.currentId++;
        return Optional.ofNullable(film);
    }

    @Override
    public Optional<Film> update(Film film) {
        if (films.size() >= film.getId()) {
            if (Optional.ofNullable(film.getLikes()).isPresent()) {
                film.setLikesNumber(film.getLikes().size());
            } else {
                film.setLikesNumber(0);
            }
            films.set((film.getId() - 1), film);
            return Optional.of(film);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного для обновления фильма не существует");
        }
    }
}
