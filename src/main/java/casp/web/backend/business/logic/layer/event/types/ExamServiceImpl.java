package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.common.DogHasHandlerReference;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.event.types.ExamV2Repository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Exam;
import casp.web.backend.deprecated.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.deprecated.reference.MemberReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamParticipant> implements ExamService {

    private final ExamV2Repository examV2Repository;

    @Autowired
    ExamServiceImpl(final CalendarService calendarService,
                    final ExamParticipantService participantService,
                    final BaseEventRepository eventRepository,
                    final MemberRepository memberRepository,
                    final MemberReferenceRepository memberReferenceRepository,
                    final CalendarRepository calendarRepository,
                    final ExamV2Repository examV2Repository,
                    final BaseParticipantRepository baseParticipantRepository,
                    final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        super(calendarService, participantService, eventRepository, memberRepository, Exam.EVENT_TYPE, memberReferenceRepository, calendarRepository, dogHasHandlerReferenceRepository, baseParticipantRepository);
        this.examV2Repository = examV2Repository;
    }

    @Override
    public void migrateDataToV2() {
        examV2Repository.deleteAll();

        eventRepository.findAllByEventType(Exam.EVENT_TYPE).forEach(e ->
                findMemberReference(e.getMemberId()).ifPresent(m -> mapAndSaveExamV2((Exam) e, m)));
    }

    private void mapAndSaveExamV2(final Exam exam, final MemberReference examMember) {
        var examV2 = BASE_EVENT_V2_MAPPER.toExam(exam);

        examV2.setMember(examMember);
        examV2.setParticipants(mapParticipants(exam));

        mapToCalendarEntries(exam.getId(), examV2);
        mapToBaseEventOptionV2(exam).ifPresent(examV2::setBaseEventOption);

        examV2Repository.save(examV2);
    }

    private Set<casp.web.backend.data.access.layer.event.participants.ExamParticipant> mapParticipants(final Exam exam) {
        return findBaseParticipants(exam.getId(), ExamParticipant.PARTICIPANT_TYPE)
                .flatMap(p -> findDogHasHandlerReference(p.getMemberOrHandlerId())
                        .map(dh -> mapParticipant((ExamParticipant) p, dh))
                        .stream())
                .collect(Collectors.toSet());
    }

    private casp.web.backend.data.access.layer.event.participants.ExamParticipant mapParticipant(final ExamParticipant participant, final DogHasHandlerReference dogHasHandler) {
        var examParticipantV2 = BASE_PARTICIPANT_V2_MAPPER.toExamParticipant(participant);
        examParticipantV2.setDogHasHandler(dogHasHandler);
        return examParticipantV2;
    }
}
