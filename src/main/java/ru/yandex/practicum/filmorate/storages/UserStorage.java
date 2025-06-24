package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;

public interface UserStorage {
    public ArrayList<User> getUsers();

    public void clearUsers();

    public User create(User user);

    public User update(User user);
}
