package ru.yandex.practicum.filmorate.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.users.DbUserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbUserService {

    private final DbUserStorage dbUserStorage;


    public List<User> getUsers() {
        return dbUserStorage.getUsers();
    }

    public void clearUsers() {
        dbUserStorage.clearUsers();
    }

    public User create(@Valid @RequestBody User user) throws ConditionsNotMetException {
        if (getUsers().stream().anyMatch(item -> item.getEmail().equals(user.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return dbUserStorage.create(user);
    }

    public User update(@Valid @RequestBody User newUser) {
        validateUnkownUser(newUser.getId());

        if (getUsers().stream().anyMatch(item -> item.getEmail().equals(newUser.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        return dbUserStorage.update(newUser);
    }

    public void addFriend(Integer userId, Integer friendId) {
        try {
            validateUnkownUser(userId);
            validateUnkownUser(friendId);

            dbUserStorage.connectFriends(userId, friendId);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedDataException("Связь друзей уже установлена");
        } catch (DataIntegrityViolationException e) {
            throw new ConditionsNotMetException("");
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {

        validateUnkownUser(userId);
        validateUnkownUser(friendId);

        dbUserStorage.removeFriendsConnection(userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        validateUnkownUser(userId);

        return dbUserStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        validateUnkownUser(userId);
        validateUnkownUser(otherUserId);

        return dbUserStorage.getCommonFriends(userId, otherUserId);
    }

    public User validateUnkownUser(Integer userId) {

        try {
            return dbUserStorage.getUser(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

}
