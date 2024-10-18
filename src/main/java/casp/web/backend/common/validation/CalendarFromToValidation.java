package casp.web.backend.common.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CalendarFromToValidation implements ConstraintValidator<CalendarFromToConstraint, CalendarValidation> {
    @Override
    public boolean isValid(CalendarValidation value, ConstraintValidatorContext context) {
        return value.getEntryFrom().isBefore(value.getEntryTo());
    }
}
