package casp.web.backend.datav2.event.types;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface CourseV2Repository extends MongoRepository<Course, UUID>, QuerydslPredicateExecutor<Course> {
}
