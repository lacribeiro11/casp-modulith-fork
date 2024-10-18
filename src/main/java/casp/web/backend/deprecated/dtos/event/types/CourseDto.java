package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.dtos.event.participants.CoTrainerDto;
import casp.web.backend.deprecated.dtos.event.participants.SpaceDto;
import casp.web.backend.deprecated.event.types.Course;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@CourseDtoSpaceConstraint
public class CourseDto extends BaseEventDto<SpaceDto> {
    @Valid
    private Set<CoTrainerDto> coTrainersToRead = new HashSet<>();
    @NotNull
    private Set<UUID> coTrainersIdToWrite = new HashSet<>();

    @PositiveOrZero
    private int spaceLimit;

    public CourseDto() {
        super(Course.EVENT_TYPE);
    }

    public Set<CoTrainerDto> getCoTrainersToRead() {
        return coTrainersToRead;
    }

    public void setCoTrainersToRead(Set<CoTrainerDto> coTrainersToRead) {
        this.coTrainersToRead = coTrainersToRead;
    }

    public Set<UUID> getCoTrainersIdToWrite() {
        return coTrainersIdToWrite;
    }

    public void setCoTrainersIdToWrite(final Set<UUID> coTrainersIdToWrite) {
        this.coTrainersIdToWrite = coTrainersIdToWrite;
    }

    public int getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(final int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }
}
