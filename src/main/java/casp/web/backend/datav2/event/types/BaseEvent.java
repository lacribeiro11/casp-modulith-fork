package casp.web.backend.datav2.event.types;


import casp.web.backend.common.BaseEventType;
import casp.web.backend.common.DogHasHandlerReference;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.commons.BaseDocument;
import casp.web.backend.data.access.layer.event.options.BaseEventOption;
import casp.web.backend.datav2.event.calendar.CalendarEntry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEvent extends BaseDocument {
    @NotNull
    BaseEventType eventType;

    @NotBlank
    String name;

    String description;

    String location;

    @Valid
    @NotNull
    @DBRef
    MemberReference member;

    @Valid
    BaseEventOption baseEventOption;

    @NotNull
    LocalDateTime minLocalDateTime;

    @NotNull
    LocalDateTime maxLocalDateTime;

    @Valid
    @NotEmpty
    List<CalendarEntry> calendarEntries = new ArrayList<>();

    BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    static boolean isMemberActive(final MemberReference member) {
        return EntityStatus.ACTIVE == member.getEntityStatus();
    }

    static boolean isDogHasHandlerActive(final DogHasHandlerReference dogHasHandler) {
        return EntityStatus.ACTIVE == dogHasHandler.getEntityStatus()
                && isMemberActive(dogHasHandler.getMember())
                && EntityStatus.ACTIVE == dogHasHandler.getDog().getEntityStatus();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MemberReference getMember() {
        return member;
    }

    public void setMember(final MemberReference member) {
        this.member = member;
    }

    public BaseEventOption getBaseEventOption() {
        return baseEventOption;
    }

    public void setBaseEventOption(BaseEventOption option) {
        this.baseEventOption = option;
    }

    public BaseEventType getEventType() {
        return eventType;
    }

    public void setEventType(BaseEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getMinLocalDateTime() {
        return minLocalDateTime;
    }

    public void setMinLocalDateTime(LocalDateTime minLocalDateTime) {
        this.minLocalDateTime = minLocalDateTime;
    }

    public LocalDateTime getMaxLocalDateTime() {
        return maxLocalDateTime;
    }

    public void setMaxLocalDateTime(LocalDateTime maxLocalDateTime) {
        this.maxLocalDateTime = maxLocalDateTime;
    }

    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(final List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }
}
