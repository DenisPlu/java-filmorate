package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Контроллер для класса Film, валидация частично с помощью spring-boot-starter-validation
@RestController
@Data
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    int currentId = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping("/films")
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: " + films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid@RequestBody Film film) throws ValidationException {
        boolean isMaxLength = film.getDescription().length() > 200;
        boolean isAfter1895 = film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
        System.out.println(isMaxLength);
        System.out.println(isAfter1895);
        if (!(isMaxLength || isAfter1895)) {
            log.debug("Добавлен фильм: {}", film);
            film.setId(currentId);
            films.add(film);
            this.currentId++;
            return film;
        } else {
            log.debug("Переданные данные не прошли валидацию. Name: {}, Description: {}, ReleaseDate: {}, Duration: {}", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            throw new ValidationException("Переданные данные не прошли валидацию, проверьте параметры: Description, ReleaseDate.");
        }
    }

    @PutMapping(value = "/films")
    public Film update(@Valid@RequestBody Film film) throws ValidationException {
        boolean isMaxLengthOfDescription = film.getDescription().length() > 200;
        boolean isAfter1895 = film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
        if (film.getId() <=  0){
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: id.");
        } else if (!(isMaxLengthOfDescription || isAfter1895)) {
            log.debug("Обновлен фильм: {}", film);
            films.set(film.getId()-1, film);
            return film;
        } else {
            log.debug("Переданные данные не прошли валидацию. Name: {}, Description: {}, ReleaseDate: {}, Duration: {}", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            throw new ValidationException("Переданные данные не прошли валидацию, проверьте параметры: name, Description, ReleaseDate, Duration.");
        }
    }
}
