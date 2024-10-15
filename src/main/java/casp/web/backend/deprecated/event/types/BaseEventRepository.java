package casp.web.backend.deprecated.event.types;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseEventRepository extends MongoRepository<BaseEvent, UUID>, QuerydslPredicateExecutor<BaseEvent>, BaseEventCustomRepository {

    Optional<BaseEvent> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusNotAndEventType(UUID memberId, EntityStatus entityStatus, String eventType);

    Set<BaseEvent> findAllByMemberIdAndEntityStatusAndEventType(UUID memberId, EntityStatus entityStatus, String eventType);

    Set<BaseEvent> findAllByEventType(String eventType);
}
