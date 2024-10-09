package casp.web.backend.deprecated.event.calendar;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CalendarCustomRepository {
    List<Calendar> findAllBetweenEventFromAndEventToAndEventTypes(LocalDate eventFrom, LocalDate eventTo, @Nullable Set<String> eventTypes);
}
