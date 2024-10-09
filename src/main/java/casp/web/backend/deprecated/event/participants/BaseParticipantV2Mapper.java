package casp.web.backend.deprecated.event.participants;

import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.participants.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface BaseParticipantV2Mapper {
    @Mapping(target = "participantType", ignore = true)
    CoTrainer toCoTrainer(casp.web.backend.deprecated.event.participants.CoTrainer coTrainer);

    @Mapping(target = "participantType", ignore = true)
    EventParticipant toEventParticipant(casp.web.backend.deprecated.event.participants.EventParticipant eventParticipant);

    @Mapping(target = "participantType", ignore = true)
    ExamParticipant toExamParticipant(casp.web.backend.deprecated.event.participants.ExamParticipant examParticipant);

    @Mapping(target = "participantType", ignore = true)
    Space toSpace(casp.web.backend.deprecated.event.participants.Space space);
}
