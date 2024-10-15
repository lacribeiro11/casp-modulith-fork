package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.common.BaseParticipantType;
import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Objects;
import java.util.UUID;

public class CoTrainer extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    public CoTrainer() {
        super(BaseParticipantType.CO_TRAINER);
    }

    public CoTrainer(final MemberReference member) {
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
        if (!(o instanceof CoTrainer coTrainer)) return false;
        return Objects.equals(member, coTrainer.member);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(member);
    }
}
