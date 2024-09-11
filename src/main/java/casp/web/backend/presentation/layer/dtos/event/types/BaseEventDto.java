package casp.web.backend.presentation.layer.dtos.event.types;


import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface BaseEventDto<P> {
    @NotEmpty
    @Valid
    List<Calendar> getCalendarEntries();

    void setCalendarEntries(@NotEmpty @Valid List<Calendar> calendarEntries);

    @NotNull
    @Valid
    Set<P> getParticipants();

    void setParticipants(@NotNull @Valid Set<P> participants);
}
