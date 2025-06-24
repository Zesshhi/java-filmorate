package ru.yandex.practicum.filmorate.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;


    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public void clearUsers() {
        inMemoryUserStorage.clearUsers();
    }

    public User create(@Valid @RequestBody User user) throws ConditionsNotMetException {
        if (getUsers().stream().anyMatch(item -> item.getEmail().equals(user.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return inMemoryUserStorage.create(user);
    }

    public User update(@Valid @RequestBody User newUser) {
        if (!inMemoryUserStorage.getUsersIds().contains(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (getUsers().stream().anyMatch(item -> item.getEmail().equals(newUser.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        return inMemoryUserStorage.update(newUser);
    }

    public void addFriend(Integer user, Integer friend) {

        validateFriends(user, friend);
        inMemoryUserStorage.addFriendToUser(user, friend);

        validateFriends(friend, user);
        inMemoryUserStorage.addFriendToUser(friend, user);
    }

    public void deleteFriend(Integer user, Integer friend) {
        deleteFriendsConnect(user, friend);
        deleteFriendsConnect(friend, user);
    }

    public void deleteFriendsConnect(Integer user, Integer friend) {
        validateFriends(user, friend);

        Set<Integer> userFriends = inMemoryUserStorage.getUserFriendsIds(user);

        inMemoryUserStorage.deleteFriendFromUser(userFriends, user, friend);
    }

    public ArrayList<User> getUserFriends(Integer user) {
        if (!inMemoryUserStorage.getUsersIds().contains(user)) {
            throw new NotFoundException("Пользователь с id = " + user + " не найден");
        }

        return inMemoryUserStorage.getUserFriends(inMemoryUserStorage.getUserFriendsIds(user));
    }

    public ArrayList<User> getCommonFriends(Integer user, Integer otherUser) {
        return inMemoryUserStorage.getCommonFriends(user, otherUser);
    }

    private void validateFriends(Integer user, Integer friend) {
        if (!inMemoryUserStorage.getUsersIds().contains(user)) {
            throw new NotFoundException("Пользователь с id = " + user + " не найден");
        }

        if (!inMemoryUserStorage.getUsersIds().contains(friend)) {
            throw new NotFoundException("Друг с id = " + friend + " не найден");
        }
    }


}
