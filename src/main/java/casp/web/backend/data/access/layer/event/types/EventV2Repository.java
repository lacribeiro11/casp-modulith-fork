package casp.web.backend.data.access.layer.event.types;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface EventV2Repository extends MongoRepository<Event, UUID>, QuerydslPredicateExecutor<Event> {
}
