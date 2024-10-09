package casp.web.backend.deprecated.event.participants;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface SpaceCustomRepository {
    Set<Space> findAllByMemberId(UUID membersId);

    Set<Space> findAllByDogId(UUID dogsId);
}
