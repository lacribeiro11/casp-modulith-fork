package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.EntityStatus;
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

    private static Space mockSpace(final EntityStatus memberEntityStatus, final EntityStatus dogEntityStatus, final EntityStatus dogHasHandlerStatus) {
        var space = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        when(space.getDogHasHandler().getMember().getEntityStatus()).thenReturn(memberEntityStatus);
        when(space.getDogHasHandler().getDog().getEntityStatus()).thenReturn(dogEntityStatus);
        when(space.getDogHasHandler().getEntityStatus()).thenReturn(dogHasHandlerStatus);
        return space;
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
        var active = mockSpace(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogInactive = mockSpace(EntityStatus.ACTIVE, EntityStatus.INACTIVE, EntityStatus.ACTIVE);
        var memberInactive = mockSpace(EntityStatus.INACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogHasHandlerInactive = mockSpace(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.INACTIVE);
        var dogDeleted = mockSpace(EntityStatus.DELETED, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var memberDeleted = mockSpace(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.ACTIVE);
        var dogHasHandlerDeleted = mockSpace(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.DELETED);
        var spaceSet = Set.of(active, dogInactive, memberInactive, dogHasHandlerInactive, dogDeleted, memberDeleted, dogHasHandlerDeleted);
        course.setSpaces(spaceSet);

        assertThat(course.getSpaces())
                .singleElement()
                .isEqualTo(active);
    }

    @Test
    void getSpaceListSize() {
        var active = mockSpace(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogInactive = mockSpace(EntityStatus.ACTIVE, EntityStatus.INACTIVE, EntityStatus.ACTIVE);
        var memberInactive = mockSpace(EntityStatus.INACTIVE, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var dogDeleted = mockSpace(EntityStatus.DELETED, EntityStatus.ACTIVE, EntityStatus.ACTIVE);
        var memberDeleted = mockSpace(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.ACTIVE);
        var spaceSet = Set.of(active, dogInactive, memberInactive, dogDeleted, memberDeleted);
        course.setSpaces(spaceSet);

        assertEquals(1, course.getSpaceListSize());
    }
}
