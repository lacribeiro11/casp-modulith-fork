package casp.web.backend.data.access.layer.event.participants;


import casp.web.backend.common.BaseParticipantType;
import casp.web.backend.common.EventResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

abstract class BaseParticipant {
    @NotNull
    BaseParticipantType participantType;

    @NotNull
    EventResponse response = EventResponse.ACCEPTED;

    BaseParticipant(final BaseParticipantType participantType) {
        this.participantType = participantType;
    }

    public BaseParticipantType getParticipantType() {
        return participantType;
    }

    public void setParticipantType(BaseParticipantType participantType) {
        this.participantType = participantType;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(EventResponse status) {
        this.response = status;
    }

    @Id
    @NotNull
    public abstract UUID getId();
}
