package casp.web.backend.datav2.member;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface MemberV2Repository extends MongoRepository<Member, UUID>, QuerydslPredicateExecutor<Member> {
}
