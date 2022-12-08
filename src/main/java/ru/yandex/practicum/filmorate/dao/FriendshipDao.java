package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Optional;
import java.util.Set;

public interface FriendshipDao {

    String addFriend(String id1, String id2);

    String updateFriendStatus(String id1, String id2);

    int removeFriend(int id1, int id2);

    int removeFriends(int id1);

    Set<Integer> getFriendsId(String id);

    Set<Friendship> getFriendList(String id);

    Optional<String> getFriendStatus(String requesterId, String donorId);
}
