package ru.yandex.practicum.filmorate;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmControllerTests {

    @Test
    void contestLoads() {
    }

    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void checkCorrectCreateFilm() {
        Film film = Film.builder()
                .name("Film1")
                .description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        filmController.create(film);
        assertEquals(filmController.getFilms().size(), 1, "Валидация не прошла при корректных параментах, фильм не создался");
    }

    @Test
    void checkCorrectUpdateFilm() {
        Film film = Film.builder().name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film2 = Film.builder().name("Film2").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film3 = Film.builder().id(2).name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        filmController.create(film);
        filmController.create(film2);
        filmController.update(film3);
        assertEquals(filmController.getFilms().get(1), film3, "Обновление фильма не прошло");
    }

    @Test
    void checkUpdateFilmWhenIdIsIncorrect() {
        Film film = Film.builder().name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film2 = Film.builder().name("Film2").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film3 = Film.builder().id(10).name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        filmController.create(film);
        filmController.create(film2);
        filmController.update(film3);
        assertEquals(filmController.getFilms().get(1), film2, "Обновление фильма прошло при некорректно заданном id");
    }

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void checkCreateFilmWhenNameIsEmpty() {
        final Film film = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assert.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkCreateFilmWhenDescriptionLengthIsMoreThen200() {
        final Film film = Film.builder().name("Film1").description("Фильм первый" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assert.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkCreateFilmWhenReleaseDateIsBefore1895() {
        final Film film = Film.builder().name("Film1").description("Фильм первый").releaseDate(LocalDate.of(1700, 1, 1)).duration(120).build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assert.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkCreateFilmWhenDurationIsNegative() {
        final Film film = Film.builder().name("Film").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(-1).build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assert.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }
}