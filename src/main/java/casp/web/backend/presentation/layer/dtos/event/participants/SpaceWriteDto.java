package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.configuration.Payment;
import casp.web.backend.configuration.PaymentConstraint;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

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
