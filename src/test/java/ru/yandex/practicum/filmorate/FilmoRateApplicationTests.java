package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testCreateUser() throws ValidationException {
        User user = User.builder()
                .id(1)
                .email("email@yandex.ru")
                .login("DEN")
                .name("Denis")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userStorage.create(user);

        assertEquals(user, userStorage.findUserById("1"));
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.findUserById("1");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}