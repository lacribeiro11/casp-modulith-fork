package casp.web.backend.deprecated.event.calendar;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CalendarFromToValidation.class})
public @interface CalendarFromToConstraint {
    String message() default "The from event date time must be before to event date time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
