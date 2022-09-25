package ru.yandex.practicum.filmorate.controller;

public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }
}
