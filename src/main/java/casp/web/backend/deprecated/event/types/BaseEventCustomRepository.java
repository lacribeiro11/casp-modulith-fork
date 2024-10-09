package casp.web.backend.deprecated.event.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseEventCustomRepository {
    Page<BaseEvent> findAllByYearAndEventType(int year, final String eventType, Pageable pageable);
}
