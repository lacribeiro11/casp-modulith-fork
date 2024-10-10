package casp.web.backend.deprecated.event.participants;

import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.member.Member;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(EventParticipant.PARTICIPANT_TYPE)
public class EventParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EVENT_PARTICIPANT";
    @Valid
    @DBRef
    private Member member;

    public EventParticipant() {
        super(PARTICIPANT_TYPE);
    }

    public EventParticipant(final BaseEvent baseEvent, final Member member) {
        super(PARTICIPANT_TYPE, member.getId(), baseEvent);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }
}
