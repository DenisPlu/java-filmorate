package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

//Контроллер для класса Film, валидация с помощью spring-boot-starter-validation
@RestController
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int currentId = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> getAll() {
        log.debug("Текущее количество фильмов: " + films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody @Valid Film film) {
            log.debug("Добавлен фильм: {}", film);
            film.setId(currentId);
            films.add(film);
            this.currentId++;
            return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody @Valid Film film) {
            log.debug("Обновлен фильм: {}", film);
            if (films.size() >= film.getId()){
                films.set(film.getId()-1, film);
                return film;
            } else {
                return null;
            }
    }

    public List<Film> getFilms() {
        return films;
    }
}
