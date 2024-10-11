package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.common.BaseEventOptionType;
import casp.web.backend.common.DogHasHandlerReference;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.event.types.DogHasHandlerReferenceRepository;
import casp.web.backend.data.access.layer.event.types.ExamV2Repository;
import casp.web.backend.data.access.layer.event.types.MemberReferenceRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Exam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private CalendarService calendarService;
    @Mock
    private ExamParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ExamV2Repository examV2Repository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private BaseParticipantRepository participantRepository;
    @Mock
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @InjectMocks
    private ExamServiceImpl examService;

    private Exam exam;

    @BeforeEach
    void setUp() {
        exam = spy(TestFixture.createExam());
    }

    @Test
    void saveBaseEvent() {
        var member = exam.getMember();
        exam.setMember(null);
        when(eventRepository.save(exam)).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findByIdAndEntityStatus(exam.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        assertSame(exam, examService.save(exam));
        verify(exam).setMember(member);
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(exam));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, exam.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(examService.getAllByYear(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(exam);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.DELETED, exam.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.INACTIVE, exam.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.ACTIVE, exam.getEntityStatus());
    }

    @Nested
    class DeleteById {
        @Test
        void exist() {
            when(eventRepository.findByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            examService.deleteById(exam.getId());

            verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
            verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
            assertSame(EntityStatus.DELETED, exam.getEntityStatus());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.deleteById(id));
        }
    }

    @Nested
    class GetBaseEventById {
        @Test
        void eventExist() {
            exam.setMember(null);
            when(eventRepository.findByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            assertSame(exam, examService.getOneById(exam.getId()));
            verify(memberRepository).findByIdAndEntityStatus(exam.getMemberId(), EntityStatus.ACTIVE);

        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.getOneById(id));
        }
    }

    @Nested
    class MigrateDataToV2 {
        private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());

        @Captor
        private ArgumentCaptor<casp.web.backend.data.access.layer.event.types.Exam> examV2Captor;
        @Mock
        private MemberReference examMember;

        private casp.web.backend.data.access.layer.event.types.Exam examV2;
        private Calendar calendar;


        @BeforeEach
        void setUp() {
            when(eventRepository.findAllByEventType(Exam.EVENT_TYPE)).thenReturn(Set.of(exam));
        }

        @Test
        void deleteAll() {
            examService.migrateDataToV2();

            verify(examV2Repository).deleteAll();
        }

        @Test
        void examMemberNotFound() {
            when(memberReferenceRepository.findById(exam.getMemberId())).thenReturn(Optional.empty());

            examService.migrateDataToV2();

            verifyNoInteractions(calendarRepository, participantRepository, dogHasHandlerReferenceRepository);
            verify(examV2Repository).deleteAll();
            verify(examV2Repository, times(0)).save(any(casp.web.backend.data.access.layer.event.types.Exam.class));
        }

        @Test
        void participants() {
            findExamMemberAndCalendar();
            var participantDogHasHandler = mock(DogHasHandlerReference.class, Answers.RETURNS_DEEP_STUBS);
            when(participantDogHasHandler.getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
            when(participantDogHasHandler.getMember().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
            when(participantDogHasHandler.getDog().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
            var examParticipant = TestFixture.createExamParticipant();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(exam.getId(), ExamParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(examParticipant));
            when(dogHasHandlerReferenceRepository.findById(examParticipant.getMemberOrHandlerId())).thenReturn(Optional.of(participantDogHasHandler));

            examService.migrateDataToV2();

            assertExamV2();
            assertThat(examV2.getParticipants()).singleElement().satisfies(s -> assertSame(participantDogHasHandler, s.getDogHasHandler()));
        }

        @Test
        void dogHasHandlerForParticipantNotFound() {
            findExamMemberAndCalendar();
            var examParticipant = TestFixture.createExamParticipant();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(exam.getId(), ExamParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(examParticipant));
            when(dogHasHandlerReferenceRepository.findById(examParticipant.getMemberOrHandlerId())).thenReturn(Optional.empty());

            examService.migrateDataToV2();

            assertExamV2();
            assertThat(examV2.getParticipants()).isEmpty();
        }


        @Test
        void daily() {
            findExamMemberAndCalendar();
            exam.setDailyOption(TestFixture.createDailyEventOption());

            examService.migrateDataToV2();

            assertExamV2();
            assertSame(BaseEventOptionType.DAILY, examV2.getBaseEventOption().getOptionType());
        }

        @Test
        void weekly() {
            findExamMemberAndCalendar();
            exam.setWeeklyOption(TestFixture.createWeeklyEventOption());

            examService.migrateDataToV2();

            assertExamV2();
            assertSame(BaseEventOptionType.WEEKLY, examV2.getBaseEventOption().getOptionType());
        }

        private void findExamMemberAndCalendar() {
            when(memberReferenceRepository.findById(exam.getMemberId())).thenReturn(Optional.of(examMember));
            calendar = TestFixture.createCalendarEntry();
            when(calendarRepository.findAllByBaseEventId(exam.getId(), SORT)).thenReturn(List.of(calendar));
        }

        private void assertExamV2() {
            verify(examV2Repository).save(examV2Captor.capture());
            examV2 = examV2Captor.getValue();
            assertEquals(exam.getId(), examV2.getId());
            assertSame(examMember, examV2.getMember());
            assertThat(examV2.getCalendarEntries()).singleElement().satisfies(ce -> assertEquals(calendar.getId(), ce.getId()));
        }
    }
}
