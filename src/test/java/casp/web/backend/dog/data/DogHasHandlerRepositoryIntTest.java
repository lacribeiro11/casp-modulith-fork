package casp.web.backend.dog.data;

import casp.web.backend.MongoIntegrationTest;
import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MongoIntegrationTest
class DogHasHandlerRepositoryIntTest {

    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberRepository;

    private DogHasHandler activeDogHasHandler1;
    private DogHasHandler inactiveDogHasHandler1;
    private DogHasHandler activeDogHasHandler2;
    private DogHasHandler inactiveDogHasHandler2;
    private UUID dogId1;
    private UUID memberId1;

    @BeforeEach
    void setUp() {
        dogHasHandlerRepository.deleteAll();
        dogReferenceRepository.deleteAll();
        memberRepository.deleteAll();
        var dog1 = dogReferenceRepository.save(ReferenceTestFixture.createDogReference());
        var dog2 = dogReferenceRepository.save(ReferenceTestFixture.createDogReference("Robert"));
        dogId1 = dog1.getId();
        var member1 = memberRepository.save(ReferenceTestFixture.createMemberReference());
        var member2 = memberRepository.save(ReferenceTestFixture.createMemberReference("Maximilian", "Mustermann"));
        memberId1 = member1.getId();

        activeDogHasHandler1 = createDogHasHandler(EntityStatus.ACTIVE, member1, dog1);
        inactiveDogHasHandler1 = createDogHasHandler(EntityStatus.INACTIVE, member2, dog1);
        activeDogHasHandler2 = createDogHasHandler(EntityStatus.ACTIVE, member2, dog2);
        inactiveDogHasHandler2 = createDogHasHandler(EntityStatus.INACTIVE, member1, dog2);
    }

    @Test
    void findAllByDogIdAndNotDeleted() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dogId1);

        assertThat(dogHasHandlers).containsExactlyInAnyOrder(activeDogHasHandler1, inactiveDogHasHandler1);
    }

    @Test
    void findAllByMemberIdAndNotDeleted() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByMemberIdAndNotDeleted(memberId1);

        assertThat(dogHasHandlers).containsExactlyInAnyOrder(activeDogHasHandler1, inactiveDogHasHandler2);
    }

    @Test
    void findAllByMemberIdAndEntityStatus() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId1, EntityStatus.ACTIVE);

        assertThat(dogHasHandlers).containsExactly(activeDogHasHandler1);
    }

    @Test
    void findByDogIdAndMemberId() {
        var dogHasHandlerOptional = dogHasHandlerRepository.findByDogIdAndMemberId(dogId1, memberId1);

        assertThat(dogHasHandlerOptional)
                .isPresent()
                .hasValue(activeDogHasHandler1);
    }

    @Test
    void findAllByDogIdAndEntityStatus() {
        var dogHasHandlers = dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId1, EntityStatus.ACTIVE);

        assertThat(dogHasHandlers).containsExactly(activeDogHasHandler1);
    }

    @Nested
    class FindAllByValue {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void nameIsEmptyOrNull(String value) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(value, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactlyInAnyOrder(activeDogHasHandler1, activeDogHasHandler2);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Rob", "Max", "Must"})
        void findByShortValue(String value) {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(value, Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler2);
        }

        @Test
        void findByDogNameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler1.getDog().getName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler1);
        }

        @Test
        void findByMemberFirstnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler1.getMember().getFirstName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler1);
        }

        @Test
        void findByMemberLastnameInCapitals() {
            var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(activeDogHasHandler1.getMember().getLastName().toUpperCase(), Pageable.unpaged());

            assertThat(dogHasHandlerPage).containsExactly(activeDogHasHandler1);
        }
    }

    @Nested
    class UniqueConstraint {

        @ParameterizedTest
        @EnumSource(value = EntityStatus.class, names = {"ACTIVE", "INACTIVE"})
        void shouldNotAllowDuplicateDogHasHandler(EntityStatus entityStatus) {
            var exception = assertThrows(DuplicateKeyException.class, () ->
                    createDogHasHandler(entityStatus, activeDogHasHandler1.getMember(), activeDogHasHandler1.getDog()));

            assertThat(exception.getMessage()).contains("E11000 duplicate key error collection");
        }

        @Test
        void shouldAllowReusingDogMemberPairAfterDeletion() {
            activeDogHasHandler1.setEntityStatus(EntityStatus.DELETED);
            dogHasHandlerRepository.save(activeDogHasHandler1);

            assertDoesNotThrow(() ->
                    createDogHasHandler(EntityStatus.ACTIVE, activeDogHasHandler1.getMember(), activeDogHasHandler1.getDog()));
        }
    }

    private DogHasHandler createDogHasHandler(EntityStatus entityStatus, MemberReference member, DogReference dog) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);
        return dogHasHandlerRepository.save(dogHasHandler);
    }
}
