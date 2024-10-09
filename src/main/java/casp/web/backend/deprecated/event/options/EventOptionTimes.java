package casp.web.backend.deprecated.event.options;

import java.time.LocalTime;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface EventOptionTimes {

    LocalTime getStartTime();

    LocalTime getEndTime();
}
