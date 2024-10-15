package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseTest {
    private Course course;

    private static CoTrainer mockCotrainer(final EntityStatus entityStatus) {
        var coTrainer = mock(CoTrainer.class, Answers.RETURNS_DEEP_STUBS);
        when(coTrainer.getMember().getEntityStatus()).thenReturn(entityStatus);
        return coTrainer;
    }

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void getCoTrainers() {
        var active = mockCotrainer(EntityStatus.ACTIVE);
        var inactive = mockCotrainer(EntityStatus.INACTIVE);
        var deleted = mockCotrainer(EntityStatus.DELETED);
        course.setCoTrainers(Set.of(active, inactive, deleted));

        assertThat(course.getCoTrainers())
                .singleElement()
                .isEqualTo(active);
    }

    @Test
    void getSpaces() {
        var active = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        var inactive = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        when(active.getDogHasHandler().isActive()).thenReturn(true);
        when(inactive.getDogHasHandler().isActive()).thenReturn(false);
        var spaceSet = Set.of(active, inactive);
        course.setSpaces(spaceSet);

        assertThat(course.getSpaces())
                .singleElement()
                .isEqualTo(active);
    }

    @Test
    void getSpaceListSize() {
        var active = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        var inactive = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        when(active.getDogHasHandler().isActive()).thenReturn(true);
        when(inactive.getDogHasHandler().isActive()).thenReturn(false);
        var spaceSet = Set.of(active, inactive);
        course.setSpaces(spaceSet);

        assertEquals(1, course.getSpaceListSize());
    }
}
