package casp.web.backend.data.access.layer.member;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MemberRepository extends BaseRepository<Member>, MemberCustomRepository {

    Page<Member> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Optional<Member> findMemberByEmail(String email);

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);

    Optional<Member> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
