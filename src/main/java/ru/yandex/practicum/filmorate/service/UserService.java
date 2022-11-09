package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Qualifier("UserDbStorage")
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;

    public String addFriend(String id1, String id2) {
        String sqlQuery = "insert into friendship (requesterID, donorID, status) values (?, ?, ?)";
        userStorage.getJdbcTemplate().update(sqlQuery, id1, id2, false);
        return id2;
    }

    public int removeFriend(int id1, int id2) {
        userStorage.getAll().get(id1 - 1).getFriendsList().remove(id2);
        userStorage.getAll().get(id2 - 1).getFriendsList().remove(id1);
        return id2;
    }

    public List<User> getCommonFriends(int id1, int id2) {
        Set<Integer> friendListId1 = new HashSet<>(userStorage.getAll().get(id1 - 1).getFriendsList());
        Set<Integer> friendListId2 = new HashSet<>(userStorage.getAll().get(id2 - 1).getFriendsList());
        friendListId1.retainAll(friendListId2);
        List<User> commonFriends = new ArrayList<>();
        for (int id : friendListId1) {
            commonFriends.add(userStorage.getAll().get(id - 1));
        }
        return commonFriends;
    }

    public UserDbStorage getUserStorage() {
        return userStorage;
    }
}
