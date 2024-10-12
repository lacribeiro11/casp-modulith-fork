package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.business.logic.layer.event.participants.BaseParticipantObserver;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.MemberReference;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.deprecated.reference.MemberReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.dog.DogHasHandlerV2Mapper.DOG_HAS_HANDLER_V2_MAPPER;

@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private static final Logger LOG = LoggerFactory.getLogger(DogHasHandlerServiceImpl.class);

    private final DogHasHandlerOldRepository dogHasHandlerOldRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final BaseParticipantObserver baseParticipantObserver;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    DogHasHandlerServiceImpl(final DogHasHandlerOldRepository dogHasHandlerOldRepository,
                             final MemberRepository memberRepository,
                             final DogRepository dogRepository,
                             final BaseParticipantObserver baseParticipantObserver,
                             final MemberReferenceRepository memberReferenceRepository,
                             final DogHasHandlerRepository dogHasHandlerRepository) {
        this.dogHasHandlerOldRepository = dogHasHandlerOldRepository;
        this.memberRepository = memberRepository;
        this.dogRepository = dogRepository;
        this.baseParticipantObserver = baseParticipantObserver;
        this.memberReferenceRepository = memberReferenceRepository;
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    private static casp.web.backend.data.access.layer.dog.DogHasHandler mapToDogHasHandlerV2(final DogHasHandler dh, final Dog dog, final MemberReference member) {
        var dogHasHandler = DOG_HAS_HANDLER_V2_MAPPER.toDogHasHandler(dh);
        dogHasHandler.setDog(DOG_HAS_HANDLER_V2_MAPPER.toDogReference(dog));
        dogHasHandler.setMember(member);
        return dogHasHandler;
    }

    // FIXME DogHasHandlerV2 will come already with dog and Member, it will be set on a facade
    @Override
    public DogHasHandler saveDogHasHandler(final DogHasHandler dogHasHandler) {
        setDogAndMember(dogHasHandler);
        return dogHasHandlerOldRepository.save(dogHasHandler);
    }

    // FIXME it will search by a DogHasHandler that is active, but also the dog and the member must be active
    @Override
    public DogHasHandler getDogHasHandlerById(final UUID id) {
        var dogHasHandler = dogHasHandlerOldRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "DogHasHandler with id %s not found or it isn't active".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
        return setDogAndMember(dogHasHandler);
    }

    // FIXME change only the repository
    @Override
    public void deleteDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerOldRepository.findAllByMemberIdAndEntityStatusIsNot(memberId, EntityStatus.DELETED)
                .forEach(this::deleteDogHasHandler);
    }

    @Override
    public void deleteDogHasHandlersByDogId(final UUID dogId) {
        dogHasHandlerOldRepository.findAllByDogIdAndEntityStatusNot(dogId, EntityStatus.DELETED)
                .forEach(this::deleteDogHasHandler);
    }

    @Override
    public Set<Dog> getDogsByMemberId(final UUID memberId) {
        return getDogHasHandlersByMemberId(memberId).stream()
                .map(dh -> setDogAndMember(dh).getDog())
                .collect(Collectors.toSet());
    }

    // FIXME change the type to MemberReference
    @Override
    public Set<Member> getMembersByDogId(final UUID dogId) {
        return getDogHasHandlersByDogId(dogId).stream()
                .map(dh -> setDogAndMember(dh).getMember())
                .collect(Collectors.toSet());
    }

    // FIXME change the type to V2, no need to set members and dogs
    @Override
    public Set<DogHasHandler> getDogHasHandlersByMemberId(final UUID memberId) {
        return setMissingMembersAndDogs(dogHasHandlerOldRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE));
    }

    // FIXME change the type to V2, no need to set members and dogs
    @Override
    public Set<DogHasHandler> getDogHasHandlersByDogId(final UUID dogId) {
        return setMissingMembersAndDogs(dogHasHandlerOldRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE));
    }

    // FIXME change the type to V2, no need to set members and dogs
    @Override
    public Set<DogHasHandler> searchByName(final String name) {
        return setMissingMembersAndDogs(dogHasHandlerOldRepository.findAllByMemberNameOrDogName(name));
    }

    // FIXME this must be a page
    @Override
    public Set<DogHasHandler> getAllDogHasHandler() {
        return dogHasHandlerOldRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
    }

    // FIXME change the type to V2
    @Override
    public Set<DogHasHandler> getDogHasHandlersByIds(final Set<UUID> handlerIds) {
        return dogHasHandlerOldRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, handlerIds);
    }

    // FIXME to be deleted
    @Override
    public Set<UUID> getDogHasHandlerIdsByMemberId(final UUID memberId) {
        return getDogHasHandlersByMemberId(memberId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    // FIXME to be deleted
    @Override
    public Set<UUID> getDogHasHandlerIdsByDogId(final UUID dogId) {
        return getDogHasHandlersByDogId(dogId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    // FIXME use this casp.web.backend.business.logic.layer.dog.DogHasHandlerServiceImpl.getDogHasHandlersByIds and map to emails
    @Override
    public Set<String> getMembersEmailByIds(final Set<UUID> handlerIds) {
        return getDogHasHandlersByIds(handlerIds).stream()
                .flatMap(dh -> Optional.ofNullable(setDogAndMember(dh).getMember())
                        .map(Member::getEmail)
                        .stream())
                .collect(Collectors.toSet());
    }

    @Override
    public void deactivateDogHasHandlersByMemberId(final UUID memberId) {
        getDogHasHandlersByMemberId(memberId).forEach(dh -> {
            baseParticipantObserver.deactivateParticipantsByMemberOrHandlerId(dh.getId());
            saveItWithNewStatus(dh, EntityStatus.INACTIVE);
        });
    }

    @Override
    public void activateDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerOldRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE).forEach(dh -> {
            baseParticipantObserver.activateParticipantsByMemberOrHandlerId(dh.getId());
            saveItWithNewStatus(dh, EntityStatus.ACTIVE);
        });
    }

    @Override
    public void migrateDataToV2() {
        var dogHasHandlerSet = dogHasHandlerOldRepository.findAll()
                .stream()
                .flatMap(dh -> dogRepository.findById(dh.getDogId())
                        .flatMap(dog -> findMemberAndMapToDogHasHandlerV2(dh, dog)).stream())
                .collect(Collectors.toSet());

        dogHasHandlerRepository.saveAll(dogHasHandlerSet);
    }

    private Optional<casp.web.backend.data.access.layer.dog.DogHasHandler> findMemberAndMapToDogHasHandlerV2(final DogHasHandler dh, final Dog dog) {
        return memberReferenceRepository.findById(dh.getMemberId()).map(memberReference -> mapToDogHasHandlerV2(dh, dog, memberReference));
    }

    private Set<DogHasHandler> setMissingMembersAndDogs(final Set<DogHasHandler> dogHasHandlers) {
        dogHasHandlers.forEach(this::setDogAndMember);
        return dogHasHandlers;
    }

    private void saveItWithNewStatus(final DogHasHandler dh, final EntityStatus entityStatus) {
        dh.setEntityStatus(entityStatus);
        dogHasHandlerOldRepository.save(dh);
    }

    private void deleteDogHasHandler(final DogHasHandler dh) {
        baseParticipantObserver.deleteParticipantsByMemberOrHandlerId(dh.getId());
        saveItWithNewStatus(dh, EntityStatus.DELETED);
    }

    private DogHasHandler setDogAndMember(final DogHasHandler dogHasHandler) {
        if (null == dogHasHandler.getMember()) {
            memberRepository.findByIdAndEntityStatus(dogHasHandler.getMemberId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setMember);
        }
        if (null == dogHasHandler.getDog()) {
            dogRepository.findDogByIdAndEntityStatus(dogHasHandler.getDogId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setDog);
        }
        return dogHasHandler;
    }
}
