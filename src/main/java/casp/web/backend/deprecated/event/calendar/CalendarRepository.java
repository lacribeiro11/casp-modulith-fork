package casp.web.backend.deprecated.event.calendar;

import casp.web.backend.common.EntityStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CalendarRepository extends MongoRepository<Calendar, UUID>, QuerydslPredicateExecutor<Calendar>, CalendarCustomRepository {

    void deleteAllByBaseEventId(UUID baseEventId);

    Optional<Calendar> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<Calendar> findAllByBaseEventIdAndEntityStatusNot(UUID baseEventId, EntityStatus entityStatus);

    Set<Calendar> findAllByBaseEventIdAndEntityStatus(UUID baseEventId, EntityStatus entityStatus);

    List<Calendar> findAllByBaseEventId(UUID baseEventId, Sort sort);
}
