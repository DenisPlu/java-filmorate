package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTests {
    FilmController filmController;

    @BeforeEach
    void setUp(){
        filmController = new FilmController();
    }

    @Test
    void checkCorrectCreateFilm() throws ValidationException {
        Film film = new Film( "Film1", "Фильм первый", LocalDate.of(2000,1,1), 120);
        filmController.create(film);
        assertEquals(filmController.getFilms().size(), 1, "Валидация не прошла при корректных параментах, фильм не создался");
    }

    @Test
    public void createUserShouldThrowExceptionWhenDescriptionLengthIsMoreThen200() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        Film film = new Film( "Film1", "Фильм первый rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                                "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                                "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                                "rrrrrrrrrrrrrrrrrrrrrrrrrrrr", LocalDate.of(2000,1,1), 120);
                        filmController.create(film);
                    }
                });
        assertEquals("Переданные данные не прошли валидацию, проверьте параметры: Description, ReleaseDate.", ex.getMessage());
    }

    @Test
    public void createUserShouldThrowExceptionWhenReleaseDateIsBefore1895() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        Film film = new Film( "Film1", "Фильм первый", LocalDate.of(1700,1,1), 120);
                        filmController.create(film);
                    }
                });
        assertEquals("Переданные данные не прошли валидацию, проверьте параметры: Description, ReleaseDate.", ex.getMessage());
    }

    @Test
    void checkUpdateFilm() throws ValidationException {
        Film film = new Film( "Film1", "Фильм первый", LocalDate.of(2000,1,1), 120);
        Film film2 = new Film( "Film2", "Фильм первый", LocalDate.of(2000,1,1), 120);
        Film film3 = new Film( 2,"Film2", "Фильм первый, обновление", LocalDate.of(2000,1,1), 120);
        filmController.create(film);
        filmController.create(film2);
        filmController.update(film3);
        assertEquals(filmController.getFilms().get(1), film3, "Обновление фильма не прошло");
    }
}