package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.event.participants.EventParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface EventParticipantMapper extends BaseParticipantMapper<EventParticipant, EventParticipantDto> {
    EventParticipantMapper EVENT_PARTICIPANT_MAPPER = Mappers.getMapper(EventParticipantMapper.class);
}
