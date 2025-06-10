package ru.yandex.practicum.filmorate.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiErrorResponse {
    private final String error;
    private final String message;
}