package ru.practicum.shareit.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndDateValidator implements ConstraintValidator<StartEndDateValidation, LocalDateTime> {
    public static LocalDateTime start;

    public boolean isValid(LocalDateTime end, ConstraintValidatorContext constraintValidatorContext) {
        return start.isBefore(end);
    }
}
