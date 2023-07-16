package ru.practicum.shareit.exception;

public class NotUniqueException extends RuntimeException {
    public NotUniqueException(String message) {
        super(message);
    }
}