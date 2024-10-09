package casp.web.backend.deprecated.event.participants;

import casp.web.backend.common.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface SpaceRepository extends MongoRepository<Space, UUID>, QuerydslPredicateExecutor<Space>, SpaceCustomRepository, BaseParticipantCustomRepository {
    Set<Space> findAllByBaseEventIdAndEntityStatus(UUID courseId, EntityStatus entityStatus);

    Set<Space> findAllByBaseEventIdAndEntityStatusNot(UUID courseId, EntityStatus entityStatus);

    void deleteAllByBaseEventId(UUID courseId);
}
