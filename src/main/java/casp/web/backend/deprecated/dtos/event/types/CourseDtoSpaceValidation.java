package casp.web.backend.deprecated.dtos.event.types;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class CourseDtoSpaceValidation implements ConstraintValidator<CourseDtoSpaceConstraint, CourseDto> {
    @Override
    public boolean isValid(CourseDto value, ConstraintValidatorContext context) {
        return CollectionUtils.isEmpty(value.getParticipantsToRead()) || value.getParticipantsToRead().size() <= value.getSpaceLimit();
    }
}
