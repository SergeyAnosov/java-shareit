package ru.practicum.shareitserver.exception.handler;
import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
}
