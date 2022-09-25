package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTests {
	UserController userController;

	@BeforeEach
	void setUp(){
		userController = new UserController();
	}

	@Test
	void checkCorrectCreateUser() throws ValidationException {
		User user = new User( "email@yandex.ru", "DEN", "Denis", LocalDate.of(2000,1,1));
		userController.create(user);
		assertEquals(userController.getUsers().size(), 1, "Валидация не прошла при корректных параментах, пользователь не создался");
	}

	@Test
	public void createUserShouldThrowExceptionWhenEmailIsEmpty() {
		ValidationException ex = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() throws ValidationException {
						User user = new User( "", "DEN", "Denis", LocalDate.of(2000,1,1));
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
						User user = new User( "emailyandex.ru", "DEN", "Denis", LocalDate.of(2000,1,1));
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
						User user = new User( "email@yandex.ru", "", "Denis", LocalDate.of(2000,1,1));
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
						User user = new User( "email@yandex.ru", "D EN", "Denis", LocalDate.of(2000,1,1));
						userController.create(user);
					}
				});
		assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
	}

	@Test
	void checkCreateUserWithEmptyName() throws ValidationException {
		User user = new User( "email@yandex.ru", "DEN", "", LocalDate.of(2000,1,1));
		userController.create(user);
		assertEquals(userController.getUsers().get(0).getName(), "DEN", "Валидация прошла при пустом name, но login не присвоился для name");
	}

	@Test
	void checkUpdateUser() throws ValidationException {
		User user = new User( "email@yandex.ru", "DEN", "", LocalDate.of(2000,1,1));
		User user2 = new User( "email@yandex.ru", "DEN2", "", LocalDate.of(2000,1,1));
		User user3 = new User( 2, "email3@yandex.ru", "DEN3", "", LocalDate.of(2000,1,1));
		userController.create(user);
		userController.create(user2);
		userController.update(user3);
		assertEquals(userController.getUsers().get(1), user3, "Обновление пользователя не прошло");
	}
}
