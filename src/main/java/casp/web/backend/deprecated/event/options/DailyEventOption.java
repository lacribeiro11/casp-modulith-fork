package casp.web.backend.deprecated.event.options;


import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.StringJoiner;

/**
 * @deprecated use {@link casp.web.backend.data.access.layer.event.options.DailyEventOption} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@EventOptionTimesConstraint
public class DailyEventOption extends BaseEventOption implements EventOptionTimes {
    private static final String OPTION_TYPE = "DAILY";

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public DailyEventOption() {
        super(OPTION_TYPE);
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DailyEventOption.class.getSimpleName() + "[", "]")
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .add("startRecurrence=" + startRecurrence)
                .add("endRecurrence=" + endRecurrence)
                .add("repeatEvery=" + repeatEvery)
                .toString();
    }
}
