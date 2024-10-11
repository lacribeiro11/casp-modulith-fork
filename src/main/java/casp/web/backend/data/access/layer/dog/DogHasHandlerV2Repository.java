package casp.web.backend.data.access.layer.dog;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DogHasHandlerV2Repository extends MongoRepository<DogHasHandler, UUID> {
}
