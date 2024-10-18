package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.dtos.member.SimpleMemberDto;
import casp.web.backend.deprecated.event.participants.CoTrainer;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class CoTrainerDto extends BaseParticipantDto {
    private SimpleMemberDto member;

    public CoTrainerDto() {
        super(CoTrainer.PARTICIPANT_TYPE);
    }

    public SimpleMemberDto getMember() {
        return member;
    }

    public void setMember(final SimpleMemberDto member) {
        this.member = member;
    }
}
