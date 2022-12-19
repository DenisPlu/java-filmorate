package ru.yandex.practicum.filmorate.daoImplStorage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FriendshipStorage implements FriendshipDao {
    private final Logger log = LoggerFactory.getLogger(FriendshipStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_FRIEND = "INSERT INTO friendship (requesterID, donorID, status) VALUES (?, ?, ?)";
    private static final String SELECT_FRIENDS = "SELECT * FROM friendship WHERE requesterID = ?";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE requesterID = ? AND donorID = ? ";
    private static final String DELETE_FRIENDS = "DELETE FROM friendship WHERE requesterID = ?";
    private static final String SELECT_FRIENDS_ID = "SELECT donorID FROM friendship WHERE requesterID = ?";
    private static final String SELECT_FRIEND = "SELECT * FROM friendship WHERE (requesterID = ?) AND (donorID = ?)";
    private static final String UPDATE_FRIEND_STATUS = "UPDATE friendship SET status=? WHERE (requesterID = ? AND donorID = ?)";

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return Friendship.builder()
                .friendId(resultSet.getInt("donorId"))
                .status(resultSet.getString("status"))
                .build();
    }

    @Override
    public String addFriend(String id1, String id2) {
        if (getFriendStatus(id1, id2).isEmpty() && getFriendStatus(id2, id1).isEmpty()) {
            jdbcTemplate.update(CREATE_FRIEND, id1, id2, false);
            return id2;
        } else if (getFriendStatus(id1, id2).isEmpty() && getFriendStatus(id2, id1).get().equals("FALSE")) {
            jdbcTemplate.update(UPDATE_FRIEND_STATUS, true, id2, id1);
            jdbcTemplate.update(CREATE_FRIEND, id1, id2, true);
            return id2;
        }
        return id2;
    }

    @Override
    public String updateFriendStatus(String id1, String id2) {
        jdbcTemplate.update(UPDATE_FRIEND_STATUS, false, id1, id2);
        System.out.println(jdbcTemplate.update(UPDATE_FRIEND_STATUS, false, id1, id2));
        return id2;
    }

    @Override
    public int removeFriend(int id1, int id2) {
        jdbcTemplate.update(DELETE_FRIEND, id1, id2);
        return id2;
    }

    @Override
    public int removeFriends(int id1) {
        jdbcTemplate.update(DELETE_FRIENDS, id1);
        return id1;
    }

    @Override
    public Set<Integer> getFriendsId(String id) {
        List<Integer> friendsIdList = jdbcTemplate.query(SELECT_FRIENDS_ID, this::mapRowToFriendId, id);
        return new HashSet<>(friendsIdList);
    }

    @Override
    public Set<Friendship> getFriendList(String id) {
        List<Friendship> friendsIdList = jdbcTemplate.query(SELECT_FRIENDS, this::mapRowToFriendship, id);
        return new HashSet<>(friendsIdList);
    }

    @Override
    public Optional<String> getFriendStatus(String requesterId, String donorId) {
        try {
            if (!Optional.of(jdbcTemplate
                    .query(SELECT_FRIEND, this::mapRowToFriendship, requesterId, donorId)).get().isEmpty()) {
                System.out.println(Optional.of(jdbcTemplate
                        .query(SELECT_FRIEND, this::mapRowToFriendship, requesterId, donorId)));
                return Optional.of(jdbcTemplate
                        .query(SELECT_FRIEND, this::mapRowToFriendship, requesterId, donorId).get(0).getStatus());
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Integer mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("donorId");
    }
}
