package casp.web.backend.common.validation;

import java.time.LocalTime;

/**
 * This interface is used because the validation at first.
 */
public interface EventOptionTimes {

    LocalTime getStartTime();

    LocalTime getEndTime();
}
