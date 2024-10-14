package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.BaseParticipantService;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.options.BaseEventOption;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipant;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.deprecated.event.calendar.CalendarV2Mapper.CALENDAR_V2_MAPPER;
import static casp.web.backend.deprecated.event.options.BaseEventOptionV2Mapper.BASE_EVENT_OPTION_V2_MAPPER;

// FIXME it will be its own service, but not reachable from outside of the package
abstract class BaseEventServiceImpl<E extends BaseEvent, P extends BaseParticipant> implements BaseEventService<E> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);
    private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());
    protected final CalendarService calendarService;
    protected final BaseParticipantService<P, E> participantService;
    protected final BaseEventRepository eventRepository;
    protected final MemberRepository memberRepository;
    protected final String eventType;
    private final MemberReferenceRepository memberReferenceRepository;
    private final CalendarRepository calendarRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final BaseParticipantRepository baseParticipantRepository;

    protected BaseEventServiceImpl(final CalendarService calendarService,
                                   final BaseParticipantService<P, E> participantService,
                                   final BaseEventRepository eventRepository,
                                   final MemberRepository memberRepository,
                                   final String eventType,
                                   final MemberReferenceRepository memberReferenceRepository,
                                   final CalendarRepository calendarRepository,
                                   final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                                   final BaseParticipantRepository baseParticipantRepository) {
        this.calendarService = calendarService;
        this.participantService = participantService;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.eventType = eventType;
        this.memberReferenceRepository = memberReferenceRepository;
        this.calendarRepository = calendarRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.baseParticipantRepository = baseParticipantRepository;
    }

    protected static Optional<BaseEventOption> mapToBaseEventOptionV2(final BaseEvent baseEvent) {
        if (null != baseEvent.getDailyOption()) {
            return Optional.of(BASE_EVENT_OPTION_V2_MAPPER.toDailyEventOption(baseEvent.getDailyOption()));
        } else if (null != baseEvent.getWeeklyOption()) {
            return Optional.of(BASE_EVENT_OPTION_V2_MAPPER.toWeeklyEventOption(baseEvent.getWeeklyOption()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(final UUID id) {
        deleteBaseEvent(getOneByIdOrThrowException(id));
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(this::deleteBaseEvent);
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(this::deactivateBaseEvent);
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(this::activateBaseEvent);

    }

    @Override
    public E getOneById(final UUID id) {
        var baseEvent = getOneByIdOrThrowException(id);
        if (baseEvent.getMember() == null) {
            setMemberIfNull(baseEvent);
        }
        return baseEvent;
    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    @Override
    public Page<E> getAllByYear(final int year, final Pageable pageable) {
        return (Page<E>) eventRepository.findAllByYearAndEventType(year, eventType, pageable);
    }

    @Override
    public E save(final E actualBaseEvent) {
        setMemberIfNull(actualBaseEvent);
        return eventRepository.save(actualBaseEvent);
    }

    protected Set<BaseEvent> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, eventType);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsActive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, eventType);
    }

    protected Set<BaseEvent> findAllByMemberIdAndIsInactive(final UUID memberId) {
        return eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, eventType);
    }

    protected void deleteBaseEvent(final BaseEvent baseEvent) {
        participantService.deleteParticipantsByBaseEventId(baseEvent.getId());
        calendarService.deleteCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.DELETED);
        eventRepository.save(baseEvent);
    }

    protected void deactivateBaseEvent(final BaseEvent baseEvent) {
        participantService.deactivateParticipantsByBaseEventId(baseEvent.getId());
        calendarService.deactivateCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.INACTIVE);
        eventRepository.save(baseEvent);
    }

    protected void activateBaseEvent(final BaseEvent baseEvent) {
        participantService.activateParticipantsByBaseEventId(baseEvent.getId());
        calendarService.activateCalendarEntriesByBaseEventId(baseEvent.getId());
        baseEvent.setEntityStatus(EntityStatus.ACTIVE);
        eventRepository.save(baseEvent);
    }

    // It cast to the correct type
    @SuppressWarnings("unchecked")
    protected E getOneByIdOrThrowException(final UUID id) {
        return (E) eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "This %s[%s] doesn't exist or it isn't active".formatted(eventType, id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    private void setMemberIfNull(final E baseEvent) {
        memberRepository.findByIdAndEntityStatus(baseEvent.getMemberId(), EntityStatus.ACTIVE)
                .ifPresent(baseEvent::setMember);
    }

    protected Optional<MemberReference> findMemberReference(final UUID memberId) {
        return memberReferenceRepository.findById(memberId);
    }

    protected <T extends casp.web.backend.data.access.layer.event.types.BaseEvent> void mapToCalendarEntries(final UUID id, final T baseEvent) {
        var calendarList = calendarRepository.findAllByBaseEventId(id, SORT);
        baseEvent.setLocation(calendarList.getFirst().getLocation());
        baseEvent.setCalendarEntries(CALENDAR_V2_MAPPER.toCalendarEntryList(calendarList));
    }

    protected Optional<DogHasHandlerReference> findDogHasHandlerReference(final UUID id) {
        return dogHasHandlerReferenceRepository.findById(id);
    }

    protected Stream<BaseParticipant> findBaseParticipants(final UUID id, final String participantType) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, participantType)
                .stream();
    }
}
