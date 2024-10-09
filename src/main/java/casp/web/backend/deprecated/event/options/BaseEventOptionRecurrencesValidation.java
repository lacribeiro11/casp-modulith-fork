package casp.web.backend.deprecated.event.options;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class BaseEventOptionRecurrencesValidation
        implements ConstraintValidator<BaseEventOptionRecurrencesConstraint, BaseEventOption> {
    @Override
    public boolean isValid(BaseEventOption value, ConstraintValidatorContext context) {
        return value.getStartRecurrence().isBefore(value.getEndRecurrence());
    }
}
