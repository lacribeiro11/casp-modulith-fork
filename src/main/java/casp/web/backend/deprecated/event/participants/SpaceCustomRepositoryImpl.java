package casp.web.backend.deprecated.event.participants;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.dog.QDogHasHandler;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Component
class SpaceCustomRepositoryImpl implements SpaceCustomRepository {
    private static final QSpace SPACE = QSpace.space;
    private static final QDogHasHandler DOG_HAS_HANDLER = QDogHasHandler.dogHasHandler;
    private final MongoOperations mongoOperations;

    @Autowired
    SpaceCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<Space> findAllByMemberId(final UUID membersId) {
        var dogHasHandlerQuery = DOG_HAS_HANDLER.entityStatus.eq(EntityStatus.ACTIVE).and(DOG_HAS_HANDLER.memberId.eq(membersId));
        return findAllSpaces(dogHasHandlerQuery);
    }

    @Override
    public Set<Space> findAllByDogId(final UUID dogsId) {
        var dogHasHandlerQuery = DOG_HAS_HANDLER.entityStatus.eq(EntityStatus.ACTIVE).and(DOG_HAS_HANDLER.dogId.eq(dogsId));
        return findAllSpaces(dogHasHandlerQuery);
    }

    private Set<Space> findAllSpaces(final BooleanExpression dogHasHandlerQuery) {
        var dogHasHandlerIds = createDogHasHandlerQuery().where(dogHasHandlerQuery).stream().map(BaseDocument::getId).collect(Collectors.toSet());
        var spaceQuery = SPACE.entityStatus.eq(EntityStatus.ACTIVE).and(SPACE.memberOrHandlerId.in(dogHasHandlerIds));
        return createSpaceQuery().where(spaceQuery).stream().collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<DogHasHandler> createDogHasHandlerQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandler.class);
    }

    private SpringDataMongodbQuery<Space> createSpaceQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Space.class);
    }
}
