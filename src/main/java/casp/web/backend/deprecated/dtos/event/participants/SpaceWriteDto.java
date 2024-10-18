package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.configuration.Payment;
import casp.web.backend.configuration.PaymentConstraint;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@PaymentConstraint
public class SpaceWriteDto extends BaseSpaceDto implements Payment {
    @NotNull
    private UUID courseId;

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(final UUID courseId) {
        this.courseId = courseId;
    }
}
