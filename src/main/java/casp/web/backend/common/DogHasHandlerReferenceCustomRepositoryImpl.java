package casp.web.backend.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class DogHasHandlerReferenceCustomRepositoryImpl implements DogHasHandlerReferenceCustomRepository {
    private static final QDogHasHandlerReference DOG_HAS_HANDLER_REFERENCE = QDogHasHandlerReference.dogHasHandlerReference;
    private final MongoOperations mongoOperations;

    @Autowired
    DogHasHandlerReferenceCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<DogHasHandlerReference> findAllByMemberId(final UUID memberId) {
        var dogHasHandlerIsActive = DOG_HAS_HANDLER_REFERENCE.entityStatus.eq(EntityStatus.ACTIVE);
        var memberCriteria = DOG_HAS_HANDLER_REFERENCE.member.id.eq(memberId);
        return query()
                .where(memberCriteria.and(dogHasHandlerIsActive))
                .stream()
                .filter(DogHasHandlerReference::isActive)
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandlerReference.class);
    }
}
