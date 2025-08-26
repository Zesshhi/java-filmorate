package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.DbUserService;

import java.util.List;

@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
public class DbUserController {

    private final DbUserService dbUserService;

    @GetMapping
    public List<User> getUsers() {
        return dbUserService.getUsers();
    }

    public void clearUsers() {
        dbUserService.clearUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ConditionsNotMetException {
        return dbUserService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return dbUserService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        dbUserService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        dbUserService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return dbUserService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return dbUserService.getCommonFriends(id, otherId);
    }

}
