package casp.web.backend.presentation.layer.dog;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.dog.DogHasHandlerDtoRequiredFields;
import casp.web.backend.common.dog.Grade;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DogHasHandlerWrite extends BaseView implements DogHasHandlerDtoRequiredFields {
    private Set<Grade> grades;
    private UUID memberId;
    private UUID dogId;

    @Override
    public Set<Grade> getGrades() {
        return grades;
    }

    @Override
    public void setGrades(final Set<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public UUID getMemberId() {
        return memberId;
    }

    @Override
    public void setMemberId(final UUID memberId) {
        this.memberId = memberId;
    }

    @Override
    public UUID getDogId() {
        return dogId;
    }

    @Override
    public void setDogId(final UUID dogId) {
        this.dogId = dogId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerWrite that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(memberId, that.memberId) && Objects.equals(dogId, that.dogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), memberId, dogId);
    }
}
