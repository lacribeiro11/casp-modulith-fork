package casp.web.backend.configuration;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BaseEventOptionRecurrencesValidation
        implements ConstraintValidator<BaseEventOptionRecurrencesConstraint, BaseEventOptionValidation> {
    @Override
    public boolean isValid(BaseEventOptionValidation value, ConstraintValidatorContext context) {
        return value.getStartRecurrence().isBefore(value.getEndRecurrence());
    }
}
