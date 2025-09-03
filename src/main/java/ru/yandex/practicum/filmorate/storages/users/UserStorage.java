package ru.yandex.practicum.filmorate.storages.users;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public List<User> getUsers();

    public Optional<User> getUser(int id);

    public void clearUsers();

    public User create(User user);

    public User update(User user);

    public void connectFriends(Integer userId, Integer friendId);

    public void removeFriendsConnection(Integer userId, Integer friendId);

    public List<User> getFriends(Integer userId);

    public List<User> getCommonFriends(Integer userId, Integer friendId);
}
