package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.event.types.EventService;
import casp.web.backend.deprecated.dtos.event.types.EventDto;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.deprecated.dtos.event.participants.EventParticipantMapper.EVENT_PARTICIPANT_MAPPER;
import static casp.web.backend.deprecated.dtos.event.types.EventMapper.EVENT_MAPPER;

@Service
class EventFacadeImpl implements EventFacade {
    private static final Logger LOG = LoggerFactory.getLogger(EventFacadeImpl.class);
    private final EventParticipantService eventParticipantService;
    private final CalendarService calendarService;
    private final EventService eventService;

    @Autowired
    EventFacadeImpl(final EventParticipantService eventParticipantService,
                    final CalendarService calendarService,
                    final EventService eventService) {
        this.eventParticipantService = eventParticipantService;
        this.calendarService = calendarService;
        this.eventService = eventService;
    }

    @Override
    public EventDto mapDocumentToDto(final BaseEvent baseEvent) {
        if (baseEvent instanceof Event event) {
            var eventDto = EVENT_MAPPER.toTarget(event);
            setEventParticipants(eventDto);
            return eventDto;
        } else {
            var msg = "The parameter %s is not an event".formatted(baseEvent.getEventType());
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void save(final EventDto eventDto) {
        var event = EVENT_MAPPER.toSource(eventDto);

        calendarService.replaceCalendarEntries(event, CALENDAR_MAPPER.toDocumentList(eventDto.getCalendarEntries()));
        eventParticipantService.replaceParticipants(event, eventDto.getParticipantsIdToWrite());

        eventService.save(event);
    }

    @Override
    public EventDto getOneById(final UUID id) {
        var event = eventService.getOneById(id);
        return mapDocumentToDto(event);
    }

    @Override
    public void deleteById(final UUID id) {
        eventService.deleteById(id);
    }

    @Override
    public Page<EventDto> getAllByYear(final int year, final Pageable pageable) {
        var eventPage = eventService.getAllByYear(year, pageable);
        return EVENT_MAPPER.toTargetPage(eventPage);
    }

    @Override
    public void migrateDataToV2() {
        eventService.migrateDataToV2();
    }

    private void setEventParticipants(final EventDto eventDto) {
        var coTrainerDtoSet = eventParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(eventDto.getId())
                .stream()
                .map(EVENT_PARTICIPANT_MAPPER::toDto)
                .collect(Collectors.toSet());

        eventDto.setParticipantsToRead(coTrainerDtoSet);
    }
}
