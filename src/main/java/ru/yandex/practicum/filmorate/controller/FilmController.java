package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
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
        return filmService.getInMemoryFilmStorage().getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable final int id) {
        try{
            return Optional.ofNullable(filmService.getInMemoryFilmStorage().getAll().get(id - 1)).get();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного фильма не существует", e);
        }
    }
    @GetMapping("/films/popular")
    public List<Film> getFilmWithBestLikes(@RequestParam (defaultValue = "10") final int count) {
        return filmService.getFilmsWithBestLikes(count);
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody @Valid Film film) {
            log.debug("Добавлен фильм: {}", film);
            filmService.getInMemoryFilmStorage().create(film);
            return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody @Valid Film film) {
            log.debug("Обновлен фильм: {}", film);
            filmService.getInMemoryFilmStorage().update(film);
            return film;
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public int addLikeToFilm(@PathVariable final int id, @PathVariable final int userId) {
        log.debug("Добавлен лайк фильма c Id: {}, пользователем с Id: {}", id, userId);
        return filmService.addLike(id-1, userId-1);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public int deleteLikeFromFilm(@PathVariable final int id, @PathVariable final int userId) {
        if (userId < 0 || userId > userService.getInMemoryUserStorage().getAll().size()){
            System.out.println(userService.getInMemoryUserStorage().getAll().size());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заданного пользователя не существует");
        } else {
            log.debug("Удален лайк у фильма c Id: {}, пользователя с Id: {}", id, userId);
            return filmService.removeLike(id - 1, userId - 1);
        }
    }

    public FilmService getFilmService() {
        return filmService;
    }
}
