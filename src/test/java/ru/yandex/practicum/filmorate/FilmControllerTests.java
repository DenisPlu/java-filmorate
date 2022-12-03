package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class FilmControllerTests {

    public final FilmService filmService;
    public final UserService userService;

    @Test
    @DirtiesContext
    void checkCreateAndFindFilmById() {
        Film film = Film.builder()
                .id(1)
                .name("Film1")
                .description("Фильм_тест checkCreateAndFindFilmById")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        Integer id = filmService.getFilmStorage().create(film).get().getId();
        assertEquals(filmService.getFilmStorage().findFilmById(id.toString()).get().getName(), film.getName(),
                "Некорректное сохранение или получение фильма по id");
    }

    @Test
    void checkCorrectUpdateFilm() {
        Film film = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .build();
        Film film2 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .build();
        Film film3 = Film.builder().id(1).name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new ArrayList<>())
                .build();
        filmService.getFilmStorage().create(film);
        filmService.getFilmStorage().create(film2);
        filmService.getFilmStorage().update(film3);
        assertEquals(filmService.getFilmStorage().findFilmById("1"), Optional.of(film3), "Обновление фильма не прошло");
    }

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    @DirtiesContext
    public void checkCreateFilmWhenNameIsEmpty() {
        final Film film = Film.builder().name("").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();
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
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assertions.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkCreateFilmWhenReleaseDateIsBefore1895() {
        final Film film = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(1700, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void checkCreateFilmWhenDurationIsNegative() {
        final Film film = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(-1)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();
        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        Assertions.assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    public void getAll() {
        final Film film1 = Film.builder().name("").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        final Film film2 = Film.builder().name("").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(130)
                .rate(2).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        final Film film3 = Film.builder().name("").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(3).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();
        filmService.getFilmStorage().create(film1);
        filmService.getFilmStorage().create(film2);
        filmService.getFilmStorage().create(film3);
        System.out.println(filmService.getFilmStorage().getAll());
        assertEquals(filmService.getFilmStorage().getAll().size(), 3);
    }

    @Test
    public void addAndRemoveLike() throws ValidationException {
        final Film film1 = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        User user = User.builder()
                .id(1)
                .email("email@yandex.ru")
                .login("DEN")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        filmService.getFilmStorage().create(film1);
        userService.getUserStorage().create(user);
        filmService.getFilmStorage().addLike("1", "1");
        System.out.println(filmService.getFilmStorage().findFilmById("1"));
        System.out.println(userService.getUserStorage().findUserById("1"));
        assertEquals(filmService.getFilmStorage().findFilmById("1").get().getLikes().size(), 1);

        filmService.getFilmStorage().removeLike(1, 1);
        assertEquals(filmService.getFilmStorage().findFilmById("1").get().getLikes().size(), 0);
    }

    @Test
    public void checkGetTwoFilmsWithBestLikes() throws ValidationException {
        final Film film1 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(0).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new ArrayList<>())
                .build();
        final Film film2 = Film.builder().name("Film2").description("Фильм 2")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(130)
                .rate(0).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new ArrayList<>())
                .build();
        final Film film3 = Film.builder().name("Film3").description("Фильм 3")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(0).mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new ArrayList<>())
                .build();
        final Film film4 = Film.builder().name("Film4").description("Фильм 4")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .likes(new HashSet<>())
                .rate(0).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .build();

        User user1 = User.builder()
                .id(1)
                .email("email@yandex.ru")
                .login("DEN")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user2 = User.builder()
                .id(2)
                .email("email2@yandex.ru")
                .login("DEN2")
                .name("Denis2")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        userService.getUserStorage().create(user1);
        userService.getUserStorage().create(user2);
        filmService.getFilmStorage().create(film1);
        filmService.getFilmStorage().create(film2);
        filmService.getFilmStorage().create(film3);
        filmService.getFilmStorage().create(film4);
        filmService.getFilmStorage().addLike("1", "1");
        filmService.getFilmStorage().addLike("2", "1");
        filmService.getFilmStorage().addLike("2", "2");
        filmService.getFilmStorage().addLike("3", "1");
        filmService.getFilmStorage().addLike("4", "1");

        List<Film> tenFilmsWithBestLikes = filmService.getFilmsWithBestLikes(2);
        assertEquals(2, tenFilmsWithBestLikes.size());
    }

    @Test
    public void getFilmMpaById() {
        final Film film1 = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
        filmService.getFilmStorage().create(film1);
        assertEquals(filmService.getFilmStorage().getMpaStorage().getFilmMpaById("1").get(), Mpa.builder().id(1).name("G").build());
    }

    @Test
    public void getFilmsMpa() {
        assertEquals(filmService.getFilmStorage().getMpaStorage().getFilmsMpa().size(), 5);
    }

    @Test
    public void getFilmsGenre() {
        assertEquals(filmService.getFilmStorage().getGenreStorage().getFilmsGenre().size(), 6);
    }

    @Test
    public void getFilmGenreById() {
        List listGenres = new ArrayList<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        assertEquals(filmService.getFilmStorage().getGenreStorage().getFilmGenreById("1").get(), listGenres.get(0));
    }

    @Test
    public void getFilmGenreByFilmId() {
        List listGenres = new ArrayList<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        final Film film1 = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .likes(new HashSet<>())
                .build();
        Integer id = filmService.getFilmStorage().create(film1).get().getId();
        assertEquals(filmService.getFilmStorage().getGenreStorage().getFilmGenreByFilmId(id.toString()), listGenres);
    }
}