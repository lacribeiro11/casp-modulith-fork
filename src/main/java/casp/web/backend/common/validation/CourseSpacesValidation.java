package casp.web.backend.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseSpacesValidation implements ConstraintValidator<CourseSpacesConstraint, CourseValidation> {
    @Override
    public boolean isValid(CourseValidation value, ConstraintValidatorContext context) {
        return value.getSpaceListSize() <= value.getSpaceLimit();
    }
}
