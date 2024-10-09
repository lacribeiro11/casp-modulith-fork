package casp.web.backend.deprecated.event.calendar;

import java.time.LocalDateTime;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CalendarValidation {
    LocalDateTime getEventFrom();

    LocalDateTime getEventTo();
}
