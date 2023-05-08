package ru.practicum.shareitserver.exception;

public class EmailExistException extends RuntimeException {
    public EmailExistException(String s) {
        super(s);
    }
}
