package casp.web.backend.deprecated.event.calendar;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, CalendarValidation> {
    @Override
    public boolean isValid(CalendarValidation value, ConstraintValidatorContext context) {
        return value.getEventFrom().isBefore(value.getEventTo());
    }
}
