package ru.yandex.practicum.filmorate.storages.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> usersFriends = new HashMap<>();


    public ArrayList<Integer> getUsersIds() {
        return new ArrayList<>(users.keySet());
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void clearUsers() {
        users.clear();
    }

    public User create(User user) throws ConditionsNotMetException {
        user.setId(getNextId());

        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        users.replace(newUser.getId(), newUser);
        return newUser;
    }

    public Set<Integer> getUserFriendsIds(Integer user) {
        if (!usersFriends.containsKey(user)) {
            usersFriends.put(user, new HashSet<>());
        }

        return usersFriends.get(user);
    }

    public ArrayList<User> getUserFriends(Set<Integer> userFriendsIds) {

        ArrayList<User> friends = new ArrayList<>();
        for (int friendId : userFriendsIds) {
            friends.add(users.get(friendId));
        }

        return friends;
    }

    public ArrayList<User> getCommonFriends(Integer user, Integer friend) {
        addUserToFriendsMapIfNotExist(user, friend);

        Set<Integer> userFriends = usersFriends.getOrDefault(user, new HashSet<>());
        Set<Integer> friendFriends = usersFriends.getOrDefault(friend, new HashSet<>());

        Set<Integer> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(friendFriends);

        return getUserFriends(commonFriends);
    }

    public void addFriendToUser(Integer user, Integer friend) {
        addUserToFriendsMapIfNotExist(user, friend);

        Set<Integer> userFriends = usersFriends.get(user);

        if (userFriends == null) {
            userFriends = new HashSet<>();
            userFriends.add(friend);
            usersFriends.put(user, userFriends);
        } else {
            userFriends.add(friend);
        }
    }


    public void deleteFriendFromUser(Set<Integer> userFriends, Integer user, Integer friend) {
        addUserToFriendsMapIfNotExist(user, friend);

        userFriends.remove(friend);
    }

    private void addUserToFriendsMapIfNotExist(Integer user, Integer friend) {

        if (!usersFriends.containsKey(user)) {
            usersFriends.put(user, new HashSet<>());
        }

        if (!usersFriends.containsKey(friend)) {
            usersFriends.put(friend, new HashSet<>());
        }

    }

    private int getNextId() {
        int currentMaxId = (int) users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
