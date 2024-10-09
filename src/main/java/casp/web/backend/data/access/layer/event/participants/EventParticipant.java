package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.common.BaseParticipantType;
import casp.web.backend.common.MemberReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Objects;
import java.util.UUID;

public class EventParticipant extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    public EventParticipant() {
        super(BaseParticipantType.EVENT_PARTICIPANT);
    }

    public EventParticipant(final MemberReference member) {
        this();
        this.member = member;
    }

    public MemberReference getMember() {
        return member;
    }

    public void setMember(final MemberReference member) {
        this.member = member;
    }

    @Override
    public UUID getId() {
        return member.getId();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof EventParticipant that)) return false;
        return Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(member);
    }
}
