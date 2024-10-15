package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.types.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class ExamParticipantServiceImpl extends BaseParticipantServiceImpl<ExamParticipant, Exam> implements ExamParticipantService {
    private final DogHasHandlerOldRepository dogHasHandlerOldRepository;

    @Autowired
    ExamParticipantServiceImpl(final BaseParticipantRepository baseParticipantRepository, final DogHasHandlerOldRepository dogHasHandlerOldRepository) {
        super(baseParticipantRepository, ExamParticipant.PARTICIPANT_TYPE);
        this.dogHasHandlerOldRepository = dogHasHandlerOldRepository;
    }

    @Override
    public void replaceParticipants(final Exam exam, final Set<UUID> participantsId) {
        var examParticipants = createExamParticipants(exam, participantsId);
        replaceParticipantsAndSetMetadata(exam, examParticipants);
    }

    @Override
    public Set<ExamParticipant> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> findDogHasHandler(s.getMemberOrHandlerId())
                        .map(dh -> {
                            s.setDogHasHandler(dh);
                            return s;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<ExamParticipant> createExamParticipants(final Exam exam, final Set<UUID> participantsId) {
        return participantsId
                .stream()
                .flatMap(id ->
                        findDogHasHandler(id).map(dh -> new ExamParticipant(exam, dh)).stream())
                .collect(Collectors.toSet());
    }

    private Optional<DogHasHandler> findDogHasHandler(final UUID dogHasHandlerId) {
        return dogHasHandlerOldRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE);
    }
}
