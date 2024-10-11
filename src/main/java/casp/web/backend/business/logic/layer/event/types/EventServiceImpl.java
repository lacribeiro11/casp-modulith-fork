package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.event.types.EventV2Repository;
import casp.web.backend.data.access.layer.event.types.MemberReferenceRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.EventParticipant;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

@Service
class EventServiceImpl extends BaseEventServiceImpl<Event, EventParticipant> implements EventService {

    private final EventV2Repository eventV2Repository;
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
        super(calendarService, participantService, eventRepository, memberRepository, Event.EVENT_TYPE, memberReferenceRepository, calendarRepository);
        this.eventV2Repository = eventV2Repository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void migrateDataToV2() {
        eventV2Repository.deleteAll();
        eventRepository.findAllByEventType(Event.EVENT_TYPE).forEach(event ->
                findMemberReference(event.getMemberId())
                        .ifPresent(m -> mapToEventV2AndSaveIt((Event) event, m)));
    }

    private void mapToEventV2AndSaveIt(final Event event, final MemberReference eventMember) {
        var eventV2 = BASE_EVENT_V2_MAPPER.toEvent(event);

        eventV2.setMember(eventMember);
        eventV2.setParticipants(mapToParticipantsV2(event));

        mapToCalendarEntries(event.getId(), eventV2);
        mapToBaseEventOptionV2(event).ifPresent(eventV2::setBaseEventOption);

        eventV2Repository.save(eventV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.EventParticipant> mapToParticipantsV2(final Event event) {
        return participantRepository.findAllByBaseEventIdAndParticipantType(event.getId(), EventParticipant.PARTICIPANT_TYPE)
                .stream()
                .flatMap(p -> findMemberReference(p.getMemberOrHandlerId())
                        .map(m -> findMemberAndMapToParticipantV2((EventParticipant) p, m))
                        .stream()
                ).collect(Collectors.toSet());
    }

    private casp.web.backend.data.access.layer.event.participants.EventParticipant findMemberAndMapToParticipantV2(final EventParticipant p, final MemberReference memberReference) {
        var eventParticipant = BASE_PARTICIPANT_V2_MAPPER.toEventParticipant(p);
        eventParticipant.setMember(memberReference);
        return eventParticipant;
    }
}
