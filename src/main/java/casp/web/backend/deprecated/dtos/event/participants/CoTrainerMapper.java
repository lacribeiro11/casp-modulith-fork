package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.event.participants.CoTrainer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface CoTrainerMapper extends BaseParticipantMapper<CoTrainer, CoTrainerDto> {
    CoTrainerMapper CO_TRAINER_MAPPER = Mappers.getMapper(CoTrainerMapper.class);
}
