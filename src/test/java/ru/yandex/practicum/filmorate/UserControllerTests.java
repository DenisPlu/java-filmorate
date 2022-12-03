package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserControllerTests {
    public final UserService userService;

    @Test
    public void testCreateAndGetUser() throws ValidationException {
        User user = User.builder()
                .id(1)
                .email("email@yandex.ru")
                .login("DEN")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        Integer id = userService.getUserStorage().create(user).get().getId();
        user.setId(id);
        assertEquals(Optional.of(user), userService.getUserStorage().findUserById(id.toString()));
    }

    @Test
    public void createUserShouldThrowExceptionWhenEmailIsEmpty() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws ValidationException {
                        User user = User.builder()
                                .email("").login("DEN")
                                .name("Denis")
                                .birthday(LocalDate.of(2000, 1, 1))
                                .build();
                        userService.getUserStorage().create(user);
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
                        userService.getUserStorage().create(user);
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
                        userService.getUserStorage().create(user);
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
                        User user = User.builder()
                                .email("email@yandex.ru")
                                .login("D EN").name("Denis")
                                .birthday(LocalDate.of(2000, 1, 1))
                                .build();
                        userService.getUserStorage().create(user);
                    }
                });
        assertEquals("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.", ex.getMessage());
    }

    @Test
    void checkCreateUserWithEmptyName() throws ValidationException {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("DEN").name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        userService.getUserStorage().create(user);
        assertEquals(userService.getUserStorage().getAll().get(0).getName(), "DEN", "Валидация прошла при пустом name, но login не присвоился для name");
    }

    @Test
    void checkUpdateUserWithoutFriends() throws ValidationException {
        User user1 = User.builder()
                .email("email@yandex.ru")
                .login("DEN1")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user2 = User.builder()
                .email("email2@yandex.ru")
                .login("DEN2")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user3 = User.builder()
                .id(1)
                .email("email3@yandex.ru")
                .login("DEN3")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(Set.of(Friendship.builder().friendId(2).status("FALSE").build()))
                .build();
        userService.getUserStorage().create(user1);
        userService.getUserStorage().create(user2);
        userService.getUserStorage().update(user3);
        assertEquals(userService.getUserStorage().findUserById("1"), Optional.of(user3), "Обновление пользователя не прошло");
    }

    @Test
    void checkUpdateUserWithFriends() throws ValidationException {
        User user1 = User.builder()
                .email("email1@yandex.ru")
                .login("DEN1")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user2 = User.builder()
                .email("email2@yandex.ru")
                .login("DEN2")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user3 = User.builder()
                .id(3)
                .email("email3@yandex.ru")
                .login("DEN3")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(Set.of(Friendship.builder().friendId(2).status("FALSE").build(),
                        Friendship.builder().friendId(1).status("FALSE").build()))
                .build();
        userService.getUserStorage().create(user1);
        userService.getUserStorage().create(user2);
        Integer id = userService.getUserStorage().create(user3).get().getId();
        User user4 = User.builder()
                .id(id)
                .email("email3@yandex.ru")
                .login("DEN3")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(Set.of(Friendship.builder().friendId(1).status("FALSE").build()))
                .build();
        userService.getUserStorage().update(user4);
        assertEquals(userService.getUserStorage().findUserById(id.toString()), Optional.of(user4), "Обновление пользователя не прошло");
    }

    @Test
    void checkUpdateUserFriendsAndFriendship() throws ValidationException {
        User user1 = User.builder()
                .email("email1@yandex.ru")
                .login("DEN1")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        User user2 = User.builder()
                .email("email2@yandex.ru")
                .login("DEN2")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        Integer id1 = userService.getUserStorage().create(user1).get().getId();
        Integer id2 = userService.getUserStorage().create(user2).get().getId();
        userService.getUserStorage().getFriendshipStorage().addFriend(id1.toString(), id2.toString());
        userService.getUserStorage().getFriendshipStorage().addFriend(id2.toString(), id1.toString());
        assertEquals(userService.getUserStorage().getFriendshipStorage().getFriendStatus(id1.toString(), id2.toString()), Optional.of("TRUE"), "Обновление дружбы не прошло");
    }

    @Test
    void getCommonFriends() throws ValidationException {
        User user1 = User.builder().id(1).email("email1@yandex.ru").login("DEN").name("Denis")
                .birthday(LocalDate.of(2000, 1, 1)).build();
        User user2 = User.builder().id(2).email("email2@yandex.ru").login("DEN2")
                .name("Denis2").birthday(LocalDate.of(2000, 1, 1)).build();
        User user3 = User.builder()
                .id(3)
                .email("email3@yandex.ru")
                .login("DEN3")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .friendsList(new HashSet<>())
                .build();
        userService.getUserStorage().create(user1);
        userService.getUserStorage().create(user2);
        userService.getUserStorage().create(user3);
        user1.setFriendsList(Set.of(Friendship.builder().friendId(3).status("false").build()));
        user2.setFriendsList(Set.of(Friendship.builder().friendId(3).status("false").build()));
        userService.getUserStorage().update(user1);
        userService.getUserStorage().update(user2);
        userService.getUserStorage().update(user3);
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
        System.out.println(userService.getUserStorage().findUserById("1"));
        System.out.println(userService.getUserStorage().getFriendshipStorage().getFriendsId("1"));
        assertEquals(userService.getCommonFriends("1", "2").get(0), user3, "Некорректное получение списка общих друзей");
    }
}
