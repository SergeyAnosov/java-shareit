package ru.practicum.shareitserver.util;

import org.springframework.http.HttpStatus;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.model.User;

public final class ExceptionThrowHandler {

    public static void throwExceptionIfUserNotExists(final User... users) {
        for (User user : users) {
            if (user == null) throw new NotFoundException(HttpStatus.NOT_FOUND, "User does not exist");
        }
    }

}