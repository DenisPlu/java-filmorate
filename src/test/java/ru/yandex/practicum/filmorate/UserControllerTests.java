package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTests {
    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void checkCorrectCreateUser() throws ValidationException {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("DEN")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);
        assertEquals(userController.getAll().size(), 1, "Валидация не прошла при корректных параментах, пользователь не создался");
    }

    @Test
    public void createUserShouldThrowExceptionWhenEmailIsEmpty() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        User user = User.builder().email("").login("DEN").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
                        userController.create(user);
                    }
                });
        assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
    }

    @Test
    public void createUserShouldThrowExceptionWhenEmailIsIncorrect() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        User user = User.builder().email("emailyandex.ru").login("DEN").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
                        userController.create(user);
                    }
                });
        assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
    }

    @Test
    public void createUserShouldThrowExceptionWhenLoginIsEmpty() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        User user = User.builder().email("email@yandex.ru").login("").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
                        userController.create(user);
                    }
                });
        assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
    }

    @Test
    public void createUserShouldThrowExceptionWhenLoginWithSpace() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        User user = User.builder().email("email@yandex.ru").login("D EN").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
                        userController.create(user);
                    }
                });
        assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
    }

    @Test
    void checkCreateUserWithEmptyName() throws ValidationException {
        User user = User.builder().email("email@yandex.ru").login("DEN").name("").birthday(LocalDate.of(2000, 1, 1)).build();
        userController.create(user);
        assertEquals(userController.getAll().get(0).getName(), "DEN", "Валидация прошла при пустом name, но login не присвоился для name");
    }

    @Test
    void checkUpdateUser() throws ValidationException {
        User user = User.builder().email("email@yandex.ru").login("DEN").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
        User user2 = User.builder().email("email@yandex.ru").login("DEN2").name("").birthday(LocalDate.of(2000, 1, 1)).build();
        User user3 = User.builder().id(2).email("email@yandex.ru").login("DEN3").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
        userController.create(user);
        userController.create(user2);
        userController.update(user3);
        assertEquals(userController.getAll().get(1), user3, "Обновление пользователя не прошло");
    }

    @Test
    void getCommonFriends() throws ValidationException {
        User user = User.builder().email("email1@yandex.ru").login("DEN").name("Denis").birthday(LocalDate.of(2000, 1, 1)).friendsList(Set.of(3, 4)).build();
        User user2 = User.builder().email("email2@yandex.ru").login("DEN2").name("").birthday(LocalDate.of(2000, 1, 1)).friendsList(Set.of(3, 5)).build();
        User user3 = User.builder().email("email3@yandex.ru").login("DEN3").name("Denis").birthday(LocalDate.of(2000, 1, 1)).build();
        userController.create(user);
        userController.create(user2);
        userController.create(user3);
        assertEquals(userController.getCommonFriends(1,2).get(0), user3, "Некорректное получение списка общих друзей");
    }
}
