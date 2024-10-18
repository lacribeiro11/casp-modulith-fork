package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.dtos.member.SimpleMemberDto;
import casp.web.backend.deprecated.event.participants.EventParticipant;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class EventParticipantDto extends BaseParticipantDto {
    private SimpleMemberDto member;

    public EventParticipantDto() {
        super(EventParticipant.PARTICIPANT_TYPE);
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }
}
