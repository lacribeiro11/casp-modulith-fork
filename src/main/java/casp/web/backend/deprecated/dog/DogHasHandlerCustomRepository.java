package casp.web.backend.deprecated.dog;

import jakarta.annotation.Nullable;

import java.util.Set;

/**
 * @deprecated It will be removed in #3.
 */
public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByMemberNameOrDogName(@Nullable String name);
}
