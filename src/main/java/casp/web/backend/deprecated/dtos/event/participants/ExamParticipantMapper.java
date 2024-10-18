package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.event.participants.ExamParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface ExamParticipantMapper extends BaseParticipantMapper<ExamParticipant, ExamParticipantDto> {
    ExamParticipantMapper EXAM_PARTICIPANT_MAPPER = Mappers.getMapper(ExamParticipantMapper.class);
}
