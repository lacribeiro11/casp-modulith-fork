package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.types.CourseV2Repository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, Space> implements CourseService {
    private final CoTrainerService coTrainerService;
    private final CourseV2Repository courseV2Repository;

    @Autowired
    CourseServiceImpl(final CalendarService calendarService,
                      final SpaceService participantService,
                      final BaseEventRepository eventRepository,
                      final CoTrainerService coTrainerService,
                      final MemberRepository memberRepository,
                      final CalendarRepository calendarRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      final MemberReferenceRepository memberReferenceRepository,
                      final CourseV2Repository courseV2Repository,
                      final BaseParticipantRepository baseParticipantRepository) {
        super(calendarService, participantService, eventRepository, memberRepository, Course.EVENT_TYPE, memberReferenceRepository, calendarRepository, dogHasHandlerReferenceRepository, baseParticipantRepository);
        this.coTrainerService = coTrainerService;
        this.courseV2Repository = courseV2Repository;
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

        eventRepository.findAllByEventType(Course.EVENT_TYPE).forEach(c ->
                findMemberReference(c.getMemberId())
                        .ifPresent(m -> mapAndSaveCourseV2((Course) c, m)));
    }

    private void deleteCourse(final BaseEvent course) {
        coTrainerService.deleteParticipantsByBaseEventId(course.getId());
        deleteBaseEvent(course);
    }

    private void mapAndSaveCourseV2(final Course course, final MemberReference courseMember) {
        var courseV2 = BASE_EVENT_V2_MAPPER.toCourse(course);

        courseV2.setMember(courseMember);
        courseV2.setSpaces(mapSpaces(course));
        courseV2.setCoTrainers(mapCoTrainers(course));

        mapToCalendarEntries(course.getId(), courseV2);
        mapToBaseEventOptionV2(course).ifPresent(courseV2::setBaseEventOption);

        courseV2Repository.save(courseV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.CoTrainer> mapCoTrainers(final Course course) {
        return findBaseParticipants(course.getId(), CoTrainer.PARTICIPANT_TYPE)
                .flatMap(ct -> findMemberReference(ct.getMemberOrHandlerId())
                        .map(m -> mapCoTrainer((CoTrainer) ct, m))
                        .stream())
                .collect(Collectors.toSet());
    }

    private casp.web.backend.data.access.layer.event.participants.CoTrainer mapCoTrainer(final CoTrainer ct, final MemberReference memberReference) {
        var coTrainerV2 = BASE_PARTICIPANT_V2_MAPPER.toCoTrainer(ct);
        coTrainerV2.setMember(memberReference);
        return coTrainerV2;
    }

    private Set<casp.web.backend.data.access.layer.event.participants.Space> mapSpaces(final Course course) {
        return findBaseParticipants(course.getId(), Space.PARTICIPANT_TYPE)
                .flatMap(s -> findDogHasHandlerReference(s.getMemberOrHandlerId())
                        .map(dh -> mapSpace((Space) s, dh))
                        .stream())
                .collect(Collectors.toSet());
    }

    private casp.web.backend.data.access.layer.event.participants.Space mapSpace(final Space space, final DogHasHandlerReference dogHasHandler) {
        var spaceV2 = BASE_PARTICIPANT_V2_MAPPER.toSpace(space);
        spaceV2.setDogHasHandler(dogHasHandler);
        return spaceV2;
    }
}
