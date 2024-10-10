package casp.web.backend.data.access.layer.event.options;


import casp.web.backend.configuration.EventOptionTimes;
import casp.web.backend.configuration.EventOptionTimesConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

// It contains too many interfaces to be a well implemented record.
@EventOptionTimesConstraint
public class WeeklyEventOptionRecurrence implements Comparable<WeeklyEventOptionRecurrence>, EventOptionTimes {
    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private static int getCompareValue(LocalTime thisLocalTime, LocalTime otherLocalTime) {
        var value = thisLocalTime.toNanoOfDay() - otherLocalTime.toNanoOfDay();
        if (value < 0) {
            return -1;
        } else if (value > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
    public int compareTo(WeeklyEventOptionRecurrence o) {
        var compareValue = getDayOfWeek().getValue() - o.getDayOfWeek().getValue();
        if (compareValue == 0) {
            compareValue = getCompareValue(getStartTime(), o.getStartTime());
        }
        if (compareValue == 0) {
            compareValue = getCompareValue(getEndTime(), o.getEndTime());
        }
        return compareValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklyEventOptionRecurrence that)) return false;
        return dayOfWeek == that.dayOfWeek && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }
}
