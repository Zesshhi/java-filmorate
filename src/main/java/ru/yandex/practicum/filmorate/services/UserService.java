package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.users.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage dbUserStorage;


    public List<User> getUsers() {
        return dbUserStorage.getUsers();
    }

    public void clearUsers() {
        dbUserStorage.clearUsers();
    }

    public User create(User user) throws ConditionsNotMetException {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return dbUserStorage.create(user);
    }

    public User update(User newUser) {
        getUser(newUser.getId());

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        return dbUserStorage.update(newUser);
    }

    public void addFriend(Integer userId, Integer friendId) {
        try {
            getUser(userId);
            getUser(friendId);

            dbUserStorage.connectFriends(userId, friendId);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedDataException("Связь друзей уже установлена");
        } catch (DataIntegrityViolationException e) {
            throw new ConditionsNotMetException("");
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {

        getUser(userId);
        getUser(friendId);

        dbUserStorage.removeFriendsConnection(userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        getUser(userId);

        return dbUserStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        getUser(userId);
        getUser(otherUserId);

        return dbUserStorage.getCommonFriends(userId, otherUserId);
    }

    public User getUser(Integer userId) {
        return dbUserStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

}
