package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.BaseParticipantObserver;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandlerV2Repository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.dog.Grade;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.dog.DogHasHandlerRepository;
import casp.web.backend.deprecated.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogHasHandlerServiceImplTest {
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Mock
    private DogRepository dogRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BaseParticipantObserver baseParticipantObserver;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogHasHandlerV2Repository dogHasHandlerV2Repository;

    @InjectMocks
    private DogHasHandlerServiceImpl dogHasHandlerService;

    private Dog dog;
    private UUID dogId;
    private Member member;
    private UUID memberId;
    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        dogHasHandler = TestFixture.createDogHasHandler();
        dog = dogHasHandler.getDog();
        dogId = dog.getId();
        member = dogHasHandler.getMember();
        memberId = member.getId();
    }

    @Test
    void deleteDogHasHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatusIsNot(memberId, EntityStatus.DELETED))
                .thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.deleteDogHasHandlersByMemberId(memberId);

        verify(baseParticipantObserver).deleteParticipantsByMemberOrHandlerId(dogHasHandler.getId());
        assertSame(EntityStatus.DELETED, dogHasHandler.getEntityStatus());
    }

    @Test
    void deleteDogHasHandlersByDogId() {
        when(dogHasHandlerRepository.findAllByDogIdAndEntityStatusNot(dogId, EntityStatus.DELETED))
                .thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.deleteDogHasHandlersByDogId(dogId);

        verify(baseParticipantObserver).deleteParticipantsByMemberOrHandlerId(dogHasHandler.getId());
        assertSame(EntityStatus.DELETED, dogHasHandler.getEntityStatus());
    }

    @Test
    void getAllDogHasHandler() {
        when(dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getAllDogHasHandler()).containsExactly(dogHasHandler);
    }

    @Test
    void getDogHasHandlersByIds() {
        when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlersByIds(Set.of(dogHasHandler.getId()))).containsExactly(dogHasHandler);
    }

    @Test
    void getDogHasHandlerIdsByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlerIdsByMemberId(memberId)).containsExactly(dogHasHandler.getId());
    }

    @Test
    void getDogHasHandlerIdsByDogId() {
        when(dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Set.of(dogHasHandler));

        assertThat(dogHasHandlerService.getDogHasHandlerIdsByDogId(dogId)).containsExactly(dogHasHandler.getId());
    }

    @Test
    void deactivateDogHasHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.deactivateDogHasHandlersByMemberId(memberId);

        verify(baseParticipantObserver).deactivateParticipantsByMemberOrHandlerId(dogHasHandler.getId());
        assertSame(EntityStatus.INACTIVE, dogHasHandler.getEntityStatus());
    }

    @Test
    void activateDogHasHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.activateDogHasHandlersByMemberId(memberId);

        verify(baseParticipantObserver).activateParticipantsByMemberOrHandlerId(dogHasHandler.getId());
        assertSame(EntityStatus.ACTIVE, dogHasHandler.getEntityStatus());
    }

    @Nested
    class MigrateDataToV2 {
        @Captor
        private ArgumentCaptor<Set<casp.web.backend.data.access.layer.dog.DogHasHandler>> dogHasHandlerV2Captor;
        private MemberReference memberReference;
        private DogHasHandler dogHasHandlerV1;

        @BeforeEach
        void setUp() {
            dogHasHandlerV1 = mock(DogHasHandler.class);
            when(dogHasHandlerV1.getDogId()).thenReturn(dogId);
            when(dogHasHandlerRepository.findAll()).thenReturn(List.of(dogHasHandlerV1));
        }

        private void dogAndMemberMocked() {
            when(dogHasHandlerV1.getMemberId()).thenReturn(memberId);
            memberReference = mock(MemberReference.class);
            when(dogRepository.findById(dogId)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findById(memberId)).thenReturn(Optional.of(memberReference));
        }

        @Test
        void dogAndMemberFound() {
            dogAndMemberMocked();

            dogHasHandlerService.migrateDataToV2();

            verify(dogHasHandlerV2Repository).saveAll(dogHasHandlerV2Captor.capture());

            assertThat(dogHasHandlerV2Captor.getValue())
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(memberReference, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }

        @Test
        void mapCard() {
            dogAndMemberMocked();
            var grade = mock(Grade.class);
            when(dogHasHandlerV1.getGrades()).thenReturn(Set.of(grade));

            dogHasHandlerService.migrateDataToV2();

            verify(dogHasHandlerV2Repository).saveAll(dogHasHandlerV2Captor.capture());

            assertThat(dogHasHandlerV2Captor.getValue())
                    .singleElement()
                    .satisfies(dh -> assertThat(dh.getGrades()).containsExactly(grade));
        }

        @Test
        void dogNotFound() {
            when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

            dogHasHandlerService.migrateDataToV2();

            verifyNoInteractions(memberReferenceRepository);
            verify(dogHasHandlerV2Repository).saveAll(dogHasHandlerV2Captor.capture());
            assertThat(dogHasHandlerV2Captor.getValue()).isEmpty();
        }

        @Test
        void memberNotFound() {
            when(dogHasHandlerV1.getMemberId()).thenReturn(memberId);
            when(dogRepository.findById(dogId)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findById(memberId)).thenReturn(Optional.empty());

            dogHasHandlerService.migrateDataToV2();

            verify(dogHasHandlerV2Repository).saveAll(dogHasHandlerV2Captor.capture());
            assertThat(dogHasHandlerV2Captor.getValue()).isEmpty();
        }
    }

    @Nested
    class GetMembersEmailByIds {
        @Test
        void memberIsNull() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));
            when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));

            assertThat(dogHasHandlerService.getMembersEmailByIds(Set.of(dogHasHandler.getId()))).containsExactly(member.getEmail());
        }

        @Test
        void memberIsNotNull() {
            when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));

            assertThat(dogHasHandlerService.getMembersEmailByIds(Set.of(dogHasHandler.getId()))).containsExactly(member.getEmail());
        }

        @Test
        void memberIsDoesNotExist() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());
            when(dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, Set.of(dogHasHandler.getId()))).thenReturn(Set.of(dogHasHandler));

            assertThat(dogHasHandlerService.getMembersEmailByIds(Set.of(dogHasHandler.getId()))).isEmpty();
        }
    }

    @Nested
    class GetDogHasHandlersByMemberId {
        @BeforeEach
        void setUp() {
            when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE))
                    .thenReturn(Set.of(dogHasHandler));
        }

        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            assertThat(dogHasHandlerService.getDogHasHandlersByMemberId(memberId))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }

        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertThat(dogHasHandlerService.getDogHasHandlersByMemberId(memberId))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }
    }

    @Nested
    class GetDogHasHandlersByDogId {
        @BeforeEach
        void setUp() {
            when(dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE))
                    .thenReturn(Set.of(dogHasHandler));
        }

        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            assertThat(dogHasHandlerService.getDogHasHandlersByDogId(dogId))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }

        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertThat(dogHasHandlerService.getDogHasHandlersByDogId(dogId))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }
    }

    @Nested
    class SearchByName {
        @BeforeEach
        void setUp() {
            when(dogHasHandlerRepository.findAllByMemberNameOrDogName(dog.getName())).thenReturn(Set.of(dogHasHandler));
        }

        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            assertThat(dogHasHandlerService.searchByName(dog.getName()))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }

        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertThat(dogHasHandlerService.searchByName(dog.getName()))
                    .singleElement()
                    .satisfies(dh -> {
                        assertSame(member, dh.getMember());
                        assertSame(dog, dh.getDog());
                    });
        }
    }

    @Nested
    class SaveDogHasHandler {
        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            dogHasHandlerService.saveDogHasHandler(dogHasHandler);

            var captor = ArgumentCaptor.forClass(DogHasHandler.class);
            verify(dogHasHandlerRepository).save(captor.capture());
            var actualDogHasHandler = captor.getValue();
            assertSame(member, actualDogHasHandler.getMember());
            assertSame(dog, actualDogHasHandler.getDog());

        }

        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            dogHasHandlerService.saveDogHasHandler(dogHasHandler);

            var captor = ArgumentCaptor.forClass(DogHasHandler.class);
            verify(dogHasHandlerRepository).save(captor.capture());
            var actualDogHasHandler = captor.getValue();
            assertSame(member, actualDogHasHandler.getMember());
            assertSame(dog, actualDogHasHandler.getDog());
        }
    }

    @Nested
    class GetDogsByMemberId {
        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE))
                    .thenReturn(Set.of(dogHasHandler));

            assertThat(dogHasHandlerService.getDogsByMemberId(memberId)).containsExactly(dog);
        }
    }

    @Nested
    class GetMembersByDogId {
        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));
            when(dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE))
                    .thenReturn(Set.of(dogHasHandler));

            assertThat(dogHasHandlerService.getMembersByDogId(dogId)).containsExactly(member);
        }
    }

    @Nested
    class GetDogHasHandlerById {
        @Test
        void handlerExist() {
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));

            assertSame(dogHasHandler, dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId()));
        }

        @Test
        void handlerDoesNotExist() {
            var dogHasHandlerId = dogHasHandler.getId();
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.getDogHasHandlerById(dogHasHandlerId));
        }

        @Test
        void dogIsEmpty() {
            dogHasHandler.setDog(null);
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));
            when(dogRepository.findDogByIdAndEntityStatus(dogId, EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));

            var actualDogHasHandler = dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId());

            assertSame(member, actualDogHasHandler.getMember());
            assertSame(dog, actualDogHasHandler.getDog());
        }

        @Test
        void memberIsEmpty() {
            dogHasHandler.setMember(null);
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));
            when(memberRepository.findByIdAndEntityStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            var actualDogHasHandler = dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId());

            assertSame(member, actualDogHasHandler.getMember());
            assertSame(dog, actualDogHasHandler.getDog());
        }
    }
}
