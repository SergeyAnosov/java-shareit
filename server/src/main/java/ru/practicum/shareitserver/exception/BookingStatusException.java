package ru.practicum.shareitserver.exception;

public class BookingStatusException extends RuntimeException {
        public BookingStatusException(final String message) {
            super(message);
        }
}
