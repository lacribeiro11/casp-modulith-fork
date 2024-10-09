package casp.web.backend.deprecated.event.options;

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
@Constraint(validatedBy = {BaseEventOptionRecurrencesValidation.class})
public @interface BaseEventOptionRecurrencesConstraint {
    String message() default "The start recurrence date must be before end recurrence date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
