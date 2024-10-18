package casp.web.backend.data.access.layer.dog;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface DogRepository extends BaseRepository<Dog>, DogCustomRepository {

    Optional<Dog> findDogByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Optional<Dog> findDogByChipNumberAndEntityStatus(String chipNumber, EntityStatus entityStatus);

    Page<Dog> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);
}
