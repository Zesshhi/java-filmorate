package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.services.DbMpaService;

import java.util.List;

@RestController
@RequestMapping("mpa")
@Slf4j
@RequiredArgsConstructor
public class DbMpaController {
    private final DbMpaService dbMpaService;

    @GetMapping
    public List<Mpa> getMpas() {
        return dbMpaService.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable int id) {
        return dbMpaService.getMpa(id);
    }
}

