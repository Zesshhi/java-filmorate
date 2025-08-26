package ru.yandex.practicum.filmorate.storages.users;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {
    public List<User> getUsers();

    public void clearUsers();

    public User create(User user);

    public User update(User user);
}
