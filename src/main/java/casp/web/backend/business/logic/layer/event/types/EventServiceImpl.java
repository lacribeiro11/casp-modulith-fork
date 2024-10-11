package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.data.access.layer.event.types.EventV2Repository;
import casp.web.backend.data.access.layer.event.types.MemberReferenceRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.EventParticipant;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.calendar.CalendarV2Mapper.CALENDAR_V2_MAPPER;
import static casp.web.backend.deprecated.event.options.BaseEventOptionV2Mapper.BASE_EVENT_OPTION_V2_MAPPER;
import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventParticipant> implements EventService {
    private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());

    private final EventV2Repository eventV2Repository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final CalendarRepository calendarRepository;
    private final BaseParticipantRepository participantRepository;

    @Autowired
    EventServiceImpl(final CalendarService calendarService,
                     final EventParticipantService participantService,
                     final BaseEventRepository eventRepository,
                     final MemberRepository memberRepository,
                     final EventV2Repository eventV2Repository,
                     final MemberReferenceRepository memberReferenceRepository,
                     final CalendarRepository calendarRepository,
                     final BaseParticipantRepository participantRepository) {
        super(calendarService, participantService, eventRepository, memberRepository, Event.EVENT_TYPE);
        this.eventV2Repository = eventV2Repository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.calendarRepository = calendarRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void migrateDataToV2() {
        eventV2Repository.deleteAll();
        eventRepository.findAllByEventType(Event.EVENT_TYPE).forEach(event -> mapToEventV2AndSaveIt((Event) event));
    }

    private void mapToEventV2AndSaveIt(final Event event) {
        var memberEventOptional = memberReferenceRepository.findById(event.getMemberId());
        if (memberEventOptional.isEmpty()) {
            return;
        }
        var eventV2 = BASE_EVENT_V2_MAPPER.toEvent(event);
        eventV2.setMember(memberEventOptional.get());

        var calendarList = calendarRepository.findAllByBaseEventId(event.getId(), SORT);
        eventV2.setLocation(calendarList.getFirst().getLocation());
        eventV2.setCalendarEntries(CALENDAR_V2_MAPPER.toCalendarEntryList(calendarList));

        eventV2.setParticipants(mapToParticipantsV2(event));

        if (null != event.getDailyOption()) {
            eventV2.setBaseEventOption(BASE_EVENT_OPTION_V2_MAPPER.toDailyEventOption(event.getDailyOption()));
        } else if (null != event.getWeeklyOption()) {
            eventV2.setBaseEventOption(BASE_EVENT_OPTION_V2_MAPPER.toWeeklyEventOption(event.getWeeklyOption()));
        }
        eventV2Repository.save(eventV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.EventParticipant> mapToParticipantsV2(final Event event) {
        return participantRepository.findAllByBaseEventIdAndParticipantType(event.getId(), EventParticipant.PARTICIPANT_TYPE)
                .stream()
                .flatMap(p -> findMemberAndMapToParticipantV2((EventParticipant) p).stream()
                ).collect(Collectors.toSet());
    }

    private Optional<casp.web.backend.data.access.layer.event.participants.EventParticipant> findMemberAndMapToParticipantV2(final EventParticipant p) {
        return memberReferenceRepository.findById(p.getMemberOrHandlerId())
                .map(m -> {
                    var eventParticipant = BASE_PARTICIPANT_V2_MAPPER.toEventParticipant(p);
                    eventParticipant.setMember(m);
                    return eventParticipant;
                });
    }
}
