package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.common.enums.BaseEventOptionType;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.types.EventV2Repository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.EventParticipant;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import casp.web.backend.deprecated.event.types.Event;
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
class EventServiceImplTest {
    @Mock
    private CalendarService calendarService;
    @Mock
    private EventParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private EventV2Repository eventV2Repository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private BaseParticipantRepository participantRepository;
    @Captor
    private ArgumentCaptor<casp.web.backend.data.access.layer.event.types.Event> eventV2Captor;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        event = spy(TestFixture.createEvent());
    }

    @Test
    void saveBaseEvent() {
        var member = event.getMember();
        event.setMember(null);
        when(eventRepository.save(event)).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findByIdAndEntityStatus(event.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        assertSame(event, eventService.save(event));
        verify(event).setMember(member);
    }

    @Nested
    class DeleteById {
        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            eventService.deleteById(event.getId());

            verify(participantService).deleteParticipantsByBaseEventId(event.getId());
            verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
            assertSame(EntityStatus.DELETED, event.getEntityStatus());
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.deleteById(id));
        }
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(event));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, event.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(eventService.getAllByYear(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(event);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, event.getEventType())).thenReturn(Set.of(event));

        eventService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(event.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.DELETED, event.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(event.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.INACTIVE, event.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(event.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.ACTIVE, event.getEntityStatus());
    }

    @Nested
    class GetBaseEventById {
        @Test
        void eventExist() {
            event.setMember(null);
            when(eventRepository.findByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            assertSame(event, eventService.getOneById(event.getId()));
            verify(memberRepository).findByIdAndEntityStatus(event.getMemberId(), EntityStatus.ACTIVE);
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.getOneById(id));
        }
    }

    @Nested
    class MigrateDataToV2 {
        private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());
        @Mock
        private MemberReference eventMember;

        private casp.web.backend.data.access.layer.event.types.Event eventV2;
        private Calendar calendar;

        @BeforeEach
        void setUp() {
            when(eventRepository.findAllByEventType(Event.EVENT_TYPE)).thenReturn(Set.of(event));
        }

        @Test
        void deleteAll() {
            eventService.migrateDataToV2();

            verify(eventV2Repository).deleteAll();
        }

        @Test
        void eventMemberNotFound() {
            when(memberReferenceRepository.findById(event.getMemberId())).thenReturn(Optional.empty());

            eventService.migrateDataToV2();

            verifyNoInteractions(calendarRepository, participantRepository);
            verify(eventV2Repository).deleteAll();
            verify(eventV2Repository, times(0)).save(any(casp.web.backend.data.access.layer.event.types.Event.class));
        }

        @Test
        void participant() {
            findEventMemberAndCalendar();
            var member = mock(MemberReference.class, Answers.RETURNS_DEEP_STUBS);
            when(member.getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
            var eventParticipant = TestFixture.createEventParticipant();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(event.getId(), EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(eventParticipant));
            when(memberReferenceRepository.findById(eventParticipant.getMemberOrHandlerId())).thenReturn(Optional.of(member));

            eventService.migrateDataToV2();

            assertCourseV2();
            assertThat(eventV2.getParticipants()).singleElement().satisfies(p -> assertSame(member, p.getMember()));
        }

        @Test
        void participantNotFound() {
            findEventMemberAndCalendar();
            var eventParticipant = TestFixture.createEventParticipant();
            when(participantRepository.findAllByBaseEventIdAndParticipantType(event.getId(), EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(eventParticipant));
            when(memberReferenceRepository.findById(eventParticipant.getMemberOrHandlerId())).thenReturn(Optional.empty());

            eventService.migrateDataToV2();

            assertCourseV2();
            assertThat(eventV2.getParticipants()).isEmpty();
        }

        @Test
        void daily() {
            findEventMemberAndCalendar();
            event.setDailyOption(TestFixture.createDailyEventOption());

            eventService.migrateDataToV2();

            assertCourseV2();
            assertSame(BaseEventOptionType.DAILY, eventV2.getBaseEventOption().getOptionType());
        }

        @Test
        void weekly() {
            findEventMemberAndCalendar();
            event.setWeeklyOption(TestFixture.createWeeklyEventOption());

            eventService.migrateDataToV2();

            assertCourseV2();
            assertSame(BaseEventOptionType.WEEKLY, eventV2.getBaseEventOption().getOptionType());
        }

        private void findEventMemberAndCalendar() {
            when(memberReferenceRepository.findById(event.getMemberId())).thenReturn(Optional.of(eventMember));
            calendar = TestFixture.createCalendarEntry();
            when(calendarRepository.findAllByBaseEventId(event.getId(), SORT)).thenReturn(List.of(calendar));
        }

        private void assertCourseV2() {
            verify(eventV2Repository).save(eventV2Captor.capture());
            eventV2 = eventV2Captor.getValue();
            assertEquals(event.getId(), eventV2.getId());
            assertSame(eventMember, eventV2.getMember());
            assertThat(eventV2.getCalendarEntries()).singleElement().satisfies(ce -> assertEquals(calendar.getId(), ce.getId()));
        }
    }

}
