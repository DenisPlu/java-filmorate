package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> getAll() {
        log.info("Текущее количество пользователей: " + filmService.getFilmStorage().getAll().size());
        return filmService.getFilmStorage().getAll();
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilmById(@PathVariable final String id) {
        return filmService.getFilmStorage().findFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getFilmWithBestLikes(@RequestParam(defaultValue = "10") final int count) {
        return filmService.getFilmsWithBestLikes(count);
    }

    @PostMapping(value = "/films")
    public Optional<Film> create(@RequestBody @Valid Film film) {
        log.debug("Добавлен фильм: {}", film);
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping(value = "/films")
    public Optional<Film> update(@RequestBody @Valid Film film) {
        log.debug("Обновлен фильм: {}", film);
        return filmService.getFilmStorage().update(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public String addLikeToFilm(@PathVariable final String id, @PathVariable final String userId) {
        log.debug("Добавлен лайк фильма c Id: {}, пользователем с Id: {}", id, userId);
        return filmService.getFilmStorage().addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public int deleteLikeFromFilm(@PathVariable final int id, @PathVariable final int userId) {
        if (userId < 0 || userId > userService.getUserStorage().getAll().size()) {
            System.out.println(userService.getUserStorage().getAll().size());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного пользователя не существует");
        } else {
            log.debug("Удален лайк у фильма c Id: {}, пользователя с Id: {}", id, userId);
            return filmService.getFilmStorage().removeLike(id, userId);
        }
    }

    @GetMapping("/genres")
    public List<Genre> getFilmGenres() {
        return filmService.getFilmStorage().getGenreStorage().getFilmsGenre();
    }

    @GetMapping("/genres/{id}")
    public Optional<Genre> getGenresById(@PathVariable final String id) {
        return filmService.getFilmStorage().getGenreStorage().getFilmGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getFilmsMpa() {
        return filmService.getFilmStorage().getMpaStorage().getFilmsMpa();
    }

    @GetMapping("/mpa/{id}")
    public Optional<Mpa> getFilmMpaById(@PathVariable final String id) {
        return filmService.getFilmStorage().getMpaStorage().getFilmMpaById(id);
    }
}
