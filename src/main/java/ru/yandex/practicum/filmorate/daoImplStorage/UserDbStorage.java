package ru.yandex.practicum.filmorate.daoImplStorage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserManualValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Getter
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;
    private final String SELECT_ALL_USERS = "SELECT * FROM users";
    private final String UPDATE_USER_BY_ID = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private final String SELECT_USER_BY_ID = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
    private final String DELETE_USER_BY_ID = "DELETE FROM employees WHERE id = ?";

    public User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friendsList(friendshipStorage.getFriendList(resultSet.getString("id")))
                .build();
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS, this::mapRowToUser);
    }

    @Override
    public Optional<User> create(User user) throws ValidationException {
        if (UserManualValidator.isValid(user)) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("id");
            String key = simpleJdbcInsert.executeAndReturnKey(user.toMap()).toString();

            if (user.getFriendsList() != null) {
                for (Friendship friendship : user.getFriendsList()) {
                    String id = user.getId().toString();
                    String friendId = friendship.getFriendId().toString();
                    friendshipStorage.addFriend(id, friendId);
                }
            }
            return findUserById(key);
        } else {
            throw new ValidationException("Ошибка валидации входных данных, проверьте параметры: Email, Login, Birthday.");
        }
    }

    @Override
    public Optional<User> update(User user) throws ValidationException {
        jdbcTemplate.update(UPDATE_USER_BY_ID,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday().toString(),
                user.getId());

        if (user.getFriendsList() != null) {
            friendshipStorage.removeFriends(user.getId());
            for (Friendship friendship : user.getFriendsList()) {
                friendshipStorage.addFriend(user.getId().toString(), friendship.getFriendId().toString());
            }
        }
        return findUserById(user.getId().toString());
    }

    @Override
    public Optional<User> findUserById(String id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_USER_BY_ID, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного пользователя не существует");
        }
    }

    public String delete(String id) {
        jdbcTemplate.update(DELETE_USER_BY_ID, id);
        return id;
    }
}
