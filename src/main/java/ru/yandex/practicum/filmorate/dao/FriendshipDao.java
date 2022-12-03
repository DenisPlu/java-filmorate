package ru.yandex.practicum.filmorate.dao;

public interface FriendshipDao {

    String addFriend(String id1, String id2);

    String updateFriendStatus(String id1, String id2);

    int removeFriend(int id1, int id2);

    int removeFriends(int id1);
}
