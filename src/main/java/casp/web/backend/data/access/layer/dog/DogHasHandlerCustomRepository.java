package casp.web.backend.data.access.layer.dog;


import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByDogIdAndNotDeleted(UUID dogId);

    Set<DogHasHandler> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<DogHasHandler> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Page<DogHasHandler> findAllByName(@Nullable String name, Pageable pageable);
}
