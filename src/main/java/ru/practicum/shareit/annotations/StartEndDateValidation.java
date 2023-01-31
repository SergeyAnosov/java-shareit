package ru.practicum.shareit.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartEndDateValidator.class)
public @interface StartEndDateValidation {
    String message() default "error Start or End date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}