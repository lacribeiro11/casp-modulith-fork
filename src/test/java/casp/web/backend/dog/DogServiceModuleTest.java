package casp.web.backend.dog;

import casp.web.backend.MongoApplicationModuleTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static casp.web.backend.dog.DogMapper.DOG_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

@MongoApplicationModuleTest
class DogServiceModuleTest {
    @Autowired
    private DogService dogService;

    @Test
    void saveDog() {
        var dog = TestFixture.createDog();

        var dogDto = dogService.saveDog(DOG_MAPPER.toTarget(dog));

        assertThat(dogDto)
                .usingRecursiveComparison()
                .comparingOnlyFields("name", "ownerName", "ownerAddress")
                .isEqualTo(dog);
    }
}
