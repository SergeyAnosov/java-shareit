package ru.practicum.shareitserver.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareitserver.exception.BookingStatusException;
import ru.practicum.shareitserver.booking.BookingController;
import ru.practicum.shareitserver.exception.BadRequestException;
import ru.practicum.shareitserver.exception.CommentException;
import ru.practicum.shareitserver.exception.WrongUserException;
import ru.practicum.shareitserver.item.ItemController;
import ru.practicum.shareitserver.user.UserController;

@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        BookingController.class})
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedState(final BookingStatusException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongUserException(final WrongUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCommentException(final CommentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }
}
