package casp.web.backend.data.access.layer.event.types;

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

    @BeforeEach
    void setUp() {
        exam = new Exam();
    }

    @Test
    void getParticipants() {
        var active = mock(ExamParticipant.class, Answers.RETURNS_DEEP_STUBS);
        var inactive = mock(ExamParticipant.class, Answers.RETURNS_DEEP_STUBS);
        when(active.getDogHasHandler().isActive()).thenReturn(true);
        when(inactive.getDogHasHandler().isActive()).thenReturn(false);
        var participants = Set.of(active, inactive);
        exam.setParticipants(participants);

        assertThat(exam.getParticipants())
                .singleElement()
                .isEqualTo(active);
    }
}
