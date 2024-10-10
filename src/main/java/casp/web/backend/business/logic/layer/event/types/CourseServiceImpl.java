package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.data.access.layer.event.types.CourseV2Repository;
import casp.web.backend.data.access.layer.event.types.DogHasHandlerReferenceRepository;
import casp.web.backend.data.access.layer.event.types.MemberReferenceRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.calendar.CalendarV2Mapper.CALENDAR_V2_MAPPER;
import static casp.web.backend.deprecated.event.options.BaseEventOptionV2Mapper.BASE_EVENT_OPTION_V2_MAPPER;
import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, Space> implements CourseService {
    private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());
    private final CoTrainerService coTrainerService;
    private final CalendarRepository calendarRepository;
    private final BaseParticipantRepository participantRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final CourseV2Repository courseV2Repository;
    private final BaseParticipantRepository baseParticipantRepository;

    @Autowired
    CourseServiceImpl(final CalendarService calendarService,
                      final SpaceService participantService,
                      final BaseEventRepository eventRepository,
                      final CoTrainerService coTrainerService,
                      final MemberRepository memberRepository,
                      final CalendarRepository calendarRepository,
                      final BaseParticipantRepository participantRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      final MemberReferenceRepository memberReferenceRepository,
                      final CourseV2Repository courseV2Repository, final BaseParticipantRepository baseParticipantRepository) {
        super(calendarService, participantService, eventRepository, memberRepository, Course.EVENT_TYPE);
        this.coTrainerService = coTrainerService;
        this.calendarRepository = calendarRepository;
        this.participantRepository = participantRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.courseV2Repository = courseV2Repository;
        this.baseParticipantRepository = baseParticipantRepository;
    }

    @Override
    public void deleteById(final UUID id) {
        deleteCourse(getOneByIdOrThrowException(id));
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(this::deleteCourse);
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(course -> {
            coTrainerService.deactivateParticipantsByBaseEventId(course.getId());
            deactivateBaseEvent(course);
        });
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(course -> {
            coTrainerService.activateParticipantsByBaseEventId(course.getId());
            activateBaseEvent(course);
        });
    }

    @Override
    public void migrateDataToV2() {
        courseV2Repository.deleteAll();

        eventRepository.findAllByEventType(Course.EVENT_TYPE).forEach(c -> mapAndSaveCourseV2((Course) c));
    }

    private void deleteCourse(final BaseEvent course) {
        coTrainerService.deleteParticipantsByBaseEventId(course.getId());
        deleteBaseEvent(course);
    }

    private void mapAndSaveCourseV2(final Course course) {
        var memberOptional = memberReferenceRepository.findById(course.getMemberId());
        if (memberOptional.isEmpty()) {
            return;
        }

        var courseV2 = BASE_EVENT_V2_MAPPER.toCourse(course);
        var calendarList = calendarRepository.findAllByBaseEventId(course.getId(), SORT);
        courseV2.setLocation(calendarList.getFirst().getLocation());
        courseV2.setCalendarEntries(CALENDAR_V2_MAPPER.toCalendarEntryList(calendarList));

        courseV2.setSpaces(mapSpaces(course));
        courseV2.setCoTrainers(mapCoTrainers(course));
        courseV2.setMember(memberOptional.get());

        if (null != course.getDailyOption()) {
            courseV2.setBaseEventOption(BASE_EVENT_OPTION_V2_MAPPER.toDailyEventOption(course.getDailyOption()));
        } else if (null != course.getWeeklyOption()) {
            courseV2.setBaseEventOption(BASE_EVENT_OPTION_V2_MAPPER.toWeeklyEventOption(course.getWeeklyOption()));
        }

        courseV2Repository.save(courseV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.CoTrainer> mapCoTrainers(final Course course) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), CoTrainer.PARTICIPANT_TYPE)
                .stream()
                .flatMap(ct -> mapCoTrainer((CoTrainer) ct).stream())
                .collect(Collectors.toSet());
    }

    private Optional<casp.web.backend.data.access.layer.event.participants.CoTrainer> mapCoTrainer(final CoTrainer ct) {
        var memberOptional = memberReferenceRepository.findById(ct.getMemberOrHandlerId());
        if (memberOptional.isEmpty()) {
            return Optional.empty();
        }
        var coTrainerV2 = BASE_PARTICIPANT_V2_MAPPER.toCoTrainer(ct);
        memberOptional.ifPresent(coTrainerV2::setMember);
        return Optional.of(coTrainerV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.Space> mapSpaces(final Course course) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(course.getId(), Space.PARTICIPANT_TYPE)
                .stream()
                .flatMap(s -> mapSpace((Space) s).stream())
                .collect(Collectors.toSet());
    }

    private Optional<casp.web.backend.data.access.layer.event.participants.Space> mapSpace(final Space space) {
        var dogHasHandlerOptional = dogHasHandlerReferenceRepository.findById(space.getMemberOrHandlerId());
        if (dogHasHandlerOptional.isEmpty()) {
            return Optional.empty();
        }
        var spaceV2 = BASE_PARTICIPANT_V2_MAPPER.toSpace(space);
        spaceV2.setDogHasHandler(dogHasHandlerOptional.get());
        return Optional.of(spaceV2);
    }
}
