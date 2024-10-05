package casp.web.backend.datav2.event.options;


import casp.web.backend.common.BaseEventOptionType;
import casp.web.backend.configuration.EventOptionTimes;
import casp.web.backend.configuration.EventOptionTimesConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@EventOptionTimesConstraint
public class DailyEventOption extends BaseEventOption implements EventOptionTimes {
    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public DailyEventOption() {
        super(BaseEventOptionType.DAILY);
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
}
