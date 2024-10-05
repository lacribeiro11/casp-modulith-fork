package casp.web.backend.datav2.dog;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface DogV2Repository extends MongoRepository<Dog, UUID>, QuerydslPredicateExecutor<Dog> {
}
