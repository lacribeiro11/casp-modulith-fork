package casp.web.backend.data.access.layer.dog;


import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class DogHasHandlerCustomRepositoryImplTest {

    @Autowired
    private DogHasHandlerOldRepository dogHasHandlerOldRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private MemberRepository memberRepository;

    private DogHasHandler dogHasHandler;
    private Dog dog;
    private Member member;

    @BeforeEach
    void setUp() {
        dogHasHandlerOldRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        dog = new Dog();
        dog.setName("Bonsai");
        dog = dogRepository.save(dog);

        member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member = memberRepository.save(member);

        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler = dogHasHandlerOldRepository.save(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByDogName() {
        assertThat(dogHasHandlerOldRepository.findAllByMemberNameOrDogName(dog.getName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByMemberFirstName() {
        assertThat(dogHasHandlerOldRepository.findAllByMemberNameOrDogName(member.getFirstName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByMemberLastName() {
        assertThat(dogHasHandlerOldRepository.findAllByMemberNameOrDogName(member.getLastName()))
                .containsExactly(dogHasHandler);
    }

    @Test
    void findDogHasHandlerByNullValue() {
        assertThat(dogHasHandlerOldRepository.findAllByMemberNameOrDogName(null))
                .containsExactly(dogHasHandler);
    }
}
