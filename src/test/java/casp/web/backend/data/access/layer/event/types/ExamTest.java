package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamTest {

    private Exam exam;

    private static ExamParticipant mockParticipant(final EntityStatus memberEntityStatus, final EntityStatus dogEntityStatus, final EntityStatus dogHasHandlerStatus) {
        var participant = mock(ExamParticipant.class, Answers.RETURNS_DEEP_STUBS);
        when(participant.getDogHasHandler().getMember().getEntityStatus()).thenReturn(memberEntityStatus);
        when(participant.getDogHasHandler().getDog().getEntityStatus()).thenReturn(dogEntityStatus);
        when(participant.getDogHasHandler().getEntityStatus()).thenReturn(dogHasHandlerStatus);
        return participant;
    }

    @BeforeEach
    void setUp() {
        exam = new Exam();
    }

    @Test
    void getParticipants() {
        var active = mockParticipant(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogInactive = mockParticipant(EntityStatus.ACTIVE, EntityStatus.INACTIVE, EntityStatus.ACTIVE);
        var memberInactive = mockParticipant(EntityStatus.INACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogHasHandlerInactive = mockParticipant(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.INACTIVE);
        var dogDeleted = mockParticipant(EntityStatus.DELETED, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var memberDeleted = mockParticipant(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.ACTIVE);
        var dogHasHandlerDeleted = mockParticipant(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.DELETED);
        var participants = Set.of(active, dogInactive, memberInactive, dogHasHandlerInactive, dogDeleted, memberDeleted, dogHasHandlerDeleted);
        exam.setParticipants(participants);

        assertThat(exam.getParticipants())
                .singleElement()
                .isEqualTo(active);
    }
}
