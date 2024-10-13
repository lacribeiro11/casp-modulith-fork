package casp.web.backend.business.logic.layer.member;


import casp.web.backend.common.EntityStatus;
import casp.web.backend.deprecated.event.types.BaseEvent;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface MemberService {


    Page<MemberDto> getMembersByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName, Pageable pageable);

    Page<MemberDto> getMembersByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    MemberDto getMemberById(UUID id);

    MemberDto saveMember(MemberDto memberDto);

    void deleteMemberById(UUID id);

    MemberDto deactivateMember(UUID id);

    MemberDto activateMember(UUID id);

    Page<MemberDto> getMembersByName(@Nullable String name, final Pageable pageable);

    Set<String> getMembersEmailByIds(Set<UUID> membersId);

    void setActiveMemberToBaseEvent(BaseEvent baseEvent);

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();
}
