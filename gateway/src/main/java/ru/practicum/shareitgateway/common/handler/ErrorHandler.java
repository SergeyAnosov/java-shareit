package ru.practicum.shareitgateway.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareitgateway.booking.BookingController;
import ru.practicum.shareitgateway.common.BookingStatusException;
import ru.practicum.shareitgateway.item.ItemController;
import ru.practicum.shareitgateway.user.UserController;

@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        BookingController.class})
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedState(final BookingStatusException e) {
        return new ErrorResponse(e.getMessage());
    }
}
