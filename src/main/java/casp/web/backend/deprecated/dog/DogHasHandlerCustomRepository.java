package casp.web.backend.deprecated.dog;

import jakarta.annotation.Nullable;

import java.util.Set;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByMemberNameOrDogName(@Nullable String name);
}
