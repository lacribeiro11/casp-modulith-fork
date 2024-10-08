package casp.web.backend.deprecated.member;

import casp.web.backend.common.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated use {@link casp.web.backend.data.access.layer.member.MemberRepository} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CardRepository extends MongoRepository<Card, UUID>, QuerydslPredicateExecutor<Card> {
    Set<Card> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Optional<Card> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<Card> findAllByMemberIdAndEntityStatusNot(UUID memberId, EntityStatus entityStatus);

    Set<Card> findAllByMemberId(UUID memberId);
}
