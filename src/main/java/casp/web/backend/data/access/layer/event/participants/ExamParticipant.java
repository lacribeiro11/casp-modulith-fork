package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.common.enums.BaseParticipantType;
import casp.web.backend.common.reference.DogHasHandlerReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Objects;
import java.util.UUID;

public class ExamParticipant extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private DogHasHandlerReference dogHasHandler;

    public ExamParticipant() {
        super(BaseParticipantType.EXAM_PARTICIPANT);
    }

    public ExamParticipant(final DogHasHandlerReference dogHasHandler) {
        this();
        this.dogHasHandler = dogHasHandler;
    }

    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerReference dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamParticipant that)) return false;
        return Objects.equals(dogHasHandler, that.dogHasHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dogHasHandler);
    }
}
