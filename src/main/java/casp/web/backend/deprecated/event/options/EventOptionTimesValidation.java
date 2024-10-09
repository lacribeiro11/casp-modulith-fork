package casp.web.backend.deprecated.event.options;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class EventOptionTimesValidation implements ConstraintValidator<EventOptionTimesConstraint, EventOptionTimes> {

    @Override
    public boolean isValid(EventOptionTimes value, ConstraintValidatorContext context) {
        return value.getStartTime().isBefore(value.getEndTime());
    }
}
