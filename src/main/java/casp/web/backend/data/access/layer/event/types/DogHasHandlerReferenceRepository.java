package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.DogHasHandlerReference;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface DogHasHandlerReferenceRepository extends MongoRepository<DogHasHandlerReference, UUID> {
}
