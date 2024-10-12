package casp.web.backend.common;

import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerReferenceCustomRepository {
    /**
     * Find all {@link DogHasHandlerReference} by member id.
     * The {@link DogHasHandlerReference} must be active. This means that both the {@link MemberReference} and the {@link DogReference} must be active.
     *
     * @param memberId the member id
     * @return a set of {@link DogHasHandlerReference} from the respective {@link MemberReference}
     */
    Set<DogHasHandlerReference> findAllByMemberId(UUID memberId);
}
