package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilmReleaseDateValidator.class)
@Documented
public @interface ValidReleaseDate {
    String message() default "{ReleaseDate in too old, it should be after 1985.12.28}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
