package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.ArrayList;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    public void clearUsers() {
        userService.clearUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ConditionsNotMetException {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

}
