package ru.yandex.practicum.filmorate;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmControllerTests {

    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()), new UserService(new InMemoryUserStorage()));
    }

    @Test
    void getFilmById() {
        Film film = Film.builder()
                .name("Film1")
                .description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        filmController.create(film);
        assertEquals(filmController.getFilmById(1), film, "Некорректное получение фильма по id");
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
        assertEquals(filmController.getFilmService().getInMemoryFilmStorage().getAll().size(), 1, "Валидация не прошла при корректных параментах, фильм не создался");
    }

    @Test
    void checkCorrectUpdateFilm() {
        Film film = Film.builder().name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film2 = Film.builder().name("Film2").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film3 = Film.builder().id(2).name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        filmController.create(film);
        filmController.create(film2);
        filmController.update(film3);
        assertEquals(filmController.getFilmService().getInMemoryFilmStorage().getAll().get(1), film3, "Обновление фильма не прошло");
    }

    @Test
    void checkUpdateFilmWhenIdIsIncorrect() {
        Film film = Film.builder().name("Film1").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film2 = Film.builder().name("Film2").description("Фильм второй").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        Film film3 = Film.builder().id(2).name("Film3").description("Фильм третий").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).build();
        filmController.create(film);
        filmController.create(film2);
        filmController.update(film3);
        assertEquals(filmController.getFilmService().getInMemoryFilmStorage().getAll().get(1), film3, "Обновление фильма прошло при некорректно заданном id");
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
        Assertions.assertTrue(validates.size() > 0);
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
        Assertions.assertTrue(validates.size() > 0);
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
        Assertions.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkGetTenFilmsWithBestLikes() {
        final Film film1 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film2 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(130).likes(Set.of(1,2,3)).build();
        final Film film3 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film4 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film5 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3,4)).build();
        final Film film6 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film7 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(160).likes(Set.of(1,2,3)).build();
        final Film film8 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film9 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film10 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3)).build();
        final Film film11 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3,4,5)).build();
        final Film film12 = Film.builder().name("").description("Фильм первый").releaseDate(LocalDate.of(2000, 1, 1)).duration(120).likes(Set.of(1,2,3,4,5,6)).build();
        filmController.create(film1);
        filmController.create(film2);
        filmController.create(film3);
        filmController.create(film4);
        filmController.create(film5);
        filmController.create(film6);
        filmController.create(film7);
        filmController.create(film8);
        filmController.create(film9);
        filmController.create(film10);
        filmController.create(film11);
        filmController.create(film12);

        List<Film> tenFilmsWithBestLikes = filmController.getFilmService().getFilmsWithBestLikes(10);
        assertEquals(10, tenFilmsWithBestLikes.size());
    }
}