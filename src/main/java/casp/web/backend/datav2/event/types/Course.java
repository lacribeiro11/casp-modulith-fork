package casp.web.backend.datav2.event.types;

import casp.web.backend.common.BaseEventType;
import casp.web.backend.configuration.CourseSpacesConstraint;
import casp.web.backend.configuration.CourseValidation;
import casp.web.backend.datav2.event.participants.CoTrainer;
import casp.web.backend.datav2.event.participants.Space;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@CourseSpacesConstraint
@QueryEntity
@Document
public class Course extends BaseEvent implements CourseValidation {
    @PositiveOrZero
    private int spaceLimit;

    @Valid
    @NotNull
    private Set<CoTrainer> coTrainers = new HashSet<>();

    @Valid
    @NotNull
    private Set<Space> spaces = new HashSet<>();

    public Course() {
        super(BaseEventType.COURSE);
    }

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    public Set<CoTrainer> getCoTrainers() {
        return coTrainers
                .stream()
                .filter(ct -> isMemberActive(ct.getMember()))
                .collect(Collectors.toSet());
    }

    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    public Set<Space> getSpaces() {
        return spaces
                .stream()
                .filter(s -> isDogHasHandlerActive(s.getDogHasHandler()))
                .collect(Collectors.toSet());
    }

    public void setSpaces(Set<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public int getSpaceListSize() {
        return getSpaces().size();
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
