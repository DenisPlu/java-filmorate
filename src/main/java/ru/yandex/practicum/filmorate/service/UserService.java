package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.daoImplStorage.FriendshipStorage;
import ru.yandex.practicum.filmorate.daoImplStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@Getter
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public String addFriend(String id1, String id2) {
        if (userStorage.findUserById(id1).isPresent() && userStorage.findUserById(id2).isPresent()) {
            return friendshipStorage.addFriend(id1, id2);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного пользователя не существует");
        }
    }

    public List<User> getFriends(String id) {
        try {
            List<Integer> friendsId = new ArrayList<>(friendshipStorage.getFriendsId(id));
            List<User> friends = new ArrayList<>();
            for (Integer friendId : friendsId) {
                friends.add(userStorage.findUserById(friendId.toString()).get());
            }
            return friends;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрошенного пользователя не существует");
        }
    }

    public List<User> getCommonFriends(String id1, String id2) {
        Set<Integer> friendListId1 = new HashSet<>(friendshipStorage.getFriendsId(id1));
        Set<Integer> friendListId2 = new HashSet<>(friendshipStorage.getFriendsId(id2));
        friendListId1.retainAll(friendListId2);
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : friendListId1) {
            commonFriends.add(userStorage.findUserById(id.toString()).get());
        }
        return commonFriends;
    }
}
