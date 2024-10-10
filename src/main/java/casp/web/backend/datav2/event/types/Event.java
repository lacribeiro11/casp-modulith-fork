package casp.web.backend.datav2.event.types;

import casp.web.backend.common.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Event extends BaseEvent {
    @Valid
    @NotNull
    private Set<EventParticipant> participants = new HashSet<>();

    public Event() {
        super(BaseEventType.EVENT);
    }

    public Set<EventParticipant> getParticipants() {
        return participants
                .stream()
                .filter(p -> isMemberActive(p.getMember()))
                .collect(Collectors.toSet());
    }

    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
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
