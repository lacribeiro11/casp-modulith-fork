package casp.web.backend.business.logic.layer.event.options;

import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.types.BaseEvent;

import java.util.ArrayList;
import java.util.List;

// FIXME map to CalendarEntry without location and remove the name service
public final class EventOptionServiceUtility {

    private EventOptionServiceUtility() {
    }

    public static <E extends BaseEvent> List<Calendar> createCalendarEntries(E baseEvent, Calendar calendarEntry) {
        List<Calendar> calendarEntries = new ArrayList<>();
        if (baseEvent.getDailyOption() != null) {
            calendarEntries = DailyOptionUtility.createCalendarEntries(calendarEntry.getLocation(), baseEvent);
        } else if (baseEvent.getWeeklyOption() != null) {
            calendarEntries = WeeklyOptionUtility.createCalendarEntries(calendarEntry.getLocation(), baseEvent);
        } else {
            calendarEntries.add(new Calendar(calendarEntry, baseEvent));
        }
        calendarEntries.sort(Calendar::compareTo);
        return calendarEntries;
    }
}
