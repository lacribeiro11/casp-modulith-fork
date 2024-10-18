package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.dtos.dog.DogHasHandlerDto;
import casp.web.backend.deprecated.event.participants.ExamParticipant;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class ExamParticipantDto extends BaseParticipantDto {
    private DogHasHandlerDto dogHasHandler;

    public ExamParticipantDto() {
        super(ExamParticipant.PARTICIPANT_TYPE);
    }

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
