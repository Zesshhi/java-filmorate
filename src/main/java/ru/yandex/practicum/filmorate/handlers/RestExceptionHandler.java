package ru.yandex.practicum.filmorate.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.responses.ApiErrorResponse;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({ConditionsNotMetException.class})
    protected ResponseEntity<Object> handleConditionsNotMetException(ConditionsNotMetException ex, WebRequest request) {
        ApiErrorResponse apiError = new ApiErrorResponse("Неправильный json", ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicatedDataException.class})
    protected ResponseEntity<Object> handleDuplicatedDataException(DuplicatedDataException ex, WebRequest request) {
        ApiErrorResponse apiError = new ApiErrorResponse("Неправильный json", ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ApiErrorResponse apiError = new ApiErrorResponse("Неправильный json", ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeException.class})
    protected ResponseEntity<Object> handleRunTimeException(RuntimeException ex, WebRequest request) {
        ApiErrorResponse apiError = new ApiErrorResponse("Ошибка сервера", ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}