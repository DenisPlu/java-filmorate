package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public int addFriend(int id1, int id2){
        inMemoryUserStorage.getAll().get(id1-1).getFriendsList().add(id2);
        inMemoryUserStorage.getAll().get(id2-1).getFriendsList().add(id1);
        return id2;
    }

    public int removeFriend(int id1, int id2){
        inMemoryUserStorage.getAll().get(id1-1).getFriendsList().remove(id2);
        inMemoryUserStorage.getAll().get(id2-1).getFriendsList().remove(id1);
        return id2;
    }

    public List<User> getCommonFriends(int id1, int id2){
        Set<Integer> friendListId1 = new HashSet<>(inMemoryUserStorage.getAll().get(id1-1).getFriendsList());
        Set<Integer> friendListId2 = new HashSet<>(inMemoryUserStorage.getAll().get(id2-1).getFriendsList());
        friendListId1.retainAll(friendListId2);
        List<User> commonFriends = new ArrayList<>();
        for (int id: friendListId1){
            commonFriends.add(inMemoryUserStorage.getAll().get(id-1));
        }
        return commonFriends;
    }

    public InMemoryUserStorage getInMemoryUserStorage() {
        return inMemoryUserStorage;
    }
}
