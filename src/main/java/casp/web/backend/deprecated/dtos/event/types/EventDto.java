package casp.web.backend.deprecated.dtos.event.types;

import casp.web.backend.deprecated.dtos.event.participants.EventParticipantDto;
import casp.web.backend.deprecated.event.types.Event;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class EventDto extends BaseEventDto<EventParticipantDto> {
    public EventDto() {
        super(Event.EVENT_TYPE);
    }
}
