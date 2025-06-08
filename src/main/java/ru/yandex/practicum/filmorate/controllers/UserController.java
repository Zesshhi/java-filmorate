package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ConditionsNotMetException {

        validateUserData(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        if (users.containsKey(newUser.getId())) {

            validateUserData(newUser);

            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }

            users.replace(newUser.getId(), newUser);
            return newUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }


    private int getNextId() {
        int currentMaxId = (int) users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    public void validateUserData(User user) throws ConditionsNotMetException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Имейл должен быть указан");
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.error("Имейл должен содержать @");
            throw new ConditionsNotMetException("Имейл должен содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин не может быть пустым");
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Логин не должен быть с пробелами");
            throw new ConditionsNotMetException("Логин не должен быть с пробелами");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }

        if (users.values().stream().anyMatch(item -> item.getEmail().equals(user.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

    }


}
