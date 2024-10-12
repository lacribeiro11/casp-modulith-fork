package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DogService {

    /**
     * Search for an active dog by its id.
     *
     * @param id of the Dog
     * @return a Dog with the given id, or throws an exception if not found
     */
    DogDto getDogById(UUID id);

    DogDto saveDog(Dog dog);

    void deleteDogById(UUID id);

    Optional<Dog> getDogByChipNumber(String chipNumber);

    Page<Dog> getDogsByNameOrOwnerName(String name, String ownerName, Pageable pageable);

    Page<Dog> getDogs(Pageable pageable);

    /**
     * Get all dogs that were not checked.
     * A dog is not checked, if its EuropeNet state is not checked and its chip number isn't empty.
     *
     * @return a page of dogs that were not checked.
     */
    Page<Dog> getDogsThatWereNotChecked(@Nullable Pageable pageable);
}
