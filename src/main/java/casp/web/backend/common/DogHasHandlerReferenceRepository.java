package casp.web.backend.common;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DogHasHandlerReferenceRepository extends MongoRepository<DogHasHandlerReference, UUID>, DogHasHandlerReferenceCustomRepository {
}
