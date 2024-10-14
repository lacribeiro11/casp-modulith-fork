package casp.web.backend.data.access.layer.dog;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerRepository extends MongoRepository<DogHasHandler, UUID>, DogHasHandlerCustomRepository {
    Optional<DogHasHandler> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Page<DogHasHandler> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Set<DogHasHandler> findAllByIdInAndEntityStatus(Set<UUID> ids, EntityStatus entityStatus);
}
