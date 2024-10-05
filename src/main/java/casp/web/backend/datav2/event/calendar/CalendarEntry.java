package casp.web.backend.datav2.event.calendar;

import casp.web.backend.configuration.CalendarFromToConstraint;
import casp.web.backend.configuration.CalendarValidation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@CalendarFromToConstraint
public class CalendarEntry implements Comparable<CalendarEntry>, CalendarValidation {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private LocalDateTime entryFrom;

    @NotNull
    private LocalDateTime entryTo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getEntryFrom() {
        return entryFrom;
    }

    public void setEntryFrom(LocalDateTime entryFrom) {
        this.entryFrom = entryFrom;
    }

    public LocalDateTime getEntryTo() {
        return entryTo;
    }

    public void setEntryTo(LocalDateTime entryTo) {
        this.entryTo = entryTo;
    }

    @Override
    public int compareTo(CalendarEntry calendar) {
        return entryFrom.compareTo(calendar.entryFrom) + entryTo.compareTo(calendar.entryTo);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEntry calendar)) return false;
        return Objects.equals(id, calendar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
