package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.deprecated.dog.DogHasHandlerV2Mapper.DOG_HAS_HANDLER_V2_MAPPER;

@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private static final Logger LOG = LoggerFactory.getLogger(DogHasHandlerServiceImpl.class);

    private final DogHasHandlerOldRepository dogHasHandlerOldRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogReferenceRepository dogReferenceRepository;
    private final DogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    DogHasHandlerServiceImpl(final DogHasHandlerOldRepository dogHasHandlerOldRepository,
                             final MemberReferenceRepository memberReferenceRepository,
                             final DogReferenceRepository dogReferenceRepository,
                             final DogHasHandlerRepository dogHasHandlerRepository) {
        this.dogHasHandlerOldRepository = dogHasHandlerOldRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.dogReferenceRepository = dogReferenceRepository;
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    private static NoSuchElementException throwNoSuchElementException(final String clazzName, final UUID id) {
        var msg = "%s with id %s not found or it isn't active".formatted(clazzName, id);
        LOG.error(msg);
        return new NoSuchElementException(msg);
    }

    @Override
    public DogHasHandlerDto saveDogHasHandler(final DogHasHandlerDto dogHasHandlerDto) {
        var dog = dogReferenceRepository.findOneByIdAndEntityStatus(dogHasHandlerDto.getDogId(), EntityStatus.ACTIVE).
                orElseThrow(() -> throwNoSuchElementException(Dog.class.getSimpleName(), dogHasHandlerDto.getDogId()));
        var member = memberReferenceRepository.findOneByIdAndEntityStatus(dogHasHandlerDto.getMemberId(), EntityStatus.ACTIVE).
                orElseThrow(() -> throwNoSuchElementException(MemberReference.class.getSimpleName(), dogHasHandlerDto.getMemberId()));

        var dogHasHandler = DOG_HAS_HANDLER_MAPPER.toSource(dogHasHandlerDto);
        dogHasHandlerRepository.findById(dogHasHandler.getId()).ifPresent(dhh -> {
            dogHasHandler.setCreated(dhh.getCreated());
            dogHasHandler.setCreatedBy(dhh.getCreatedBy());
        });
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);

        return DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandlerRepository.save(dogHasHandler));
    }

    @Override
    public DogHasHandlerDto getDogHasHandlerById(final UUID id) {
        var dogHasHandler = dogHasHandlerRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> throwNoSuchElementException(DogHasHandler.class.getSimpleName(), id));
        return DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandler);
    }

    @Override
    public void deleteDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.DELETED));
    }

    @Override
    public void deleteDogHasHandlersByDogId(final UUID dogId) {
        dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dogId)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.DELETED));
    }

    @Override
    public void deleteDogHasHandlerById(final UUID id) {
        var dogHasHandler = dogHasHandlerRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> throwNoSuchElementException(DogHasHandler.class.getSimpleName(), id));

        saveItWithNewStatus(dogHasHandler, EntityStatus.DELETED);
    }

    @Override
    public Page<DogHasHandlerDto> searchByName(@Nullable final String name, final Pageable pageable) {
        var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(name, pageable);
        return DOG_HAS_HANDLER_MAPPER.toTargetPage(dogHasHandlerPage);
    }

    @Override
    public Page<DogHasHandlerDto> getAllDogHasHandlers(final Pageable pageable) {
        var dogHasHandlerPage = dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
        return DOG_HAS_HANDLER_MAPPER.toTargetPage(dogHasHandlerPage);
    }

    @Override
    public Set<DogHasHandlerDto> getDogHasHandlersByIds(final Set<UUID> ids) {
        return DOG_HAS_HANDLER_MAPPER.toTargetSet(getActiveDogHasHandlerSet(ids));
    }

    @Override
    public Set<String> getEmailsByDogHasHandlersIds(final Set<UUID> ids) {
        return getActiveDogHasHandlerSet(ids)
                .stream()
                .map(dhh -> dhh.getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void deactivateDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.INACTIVE));
    }

    @Override
    public void activateDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.ACTIVE));
    }

    @Override
    public void migrateDataToV2() {
        var dogHasHandlerSet = dogHasHandlerOldRepository.findAll()
                .stream()
                .flatMap(dh -> dogReferenceRepository.findById(dh.getDogId())
                        .flatMap(dog -> findMemberAndMapToDogHasHandlerV2(dh, dog)).stream())
                .collect(Collectors.toSet());

        dogHasHandlerRepository.saveAll(dogHasHandlerSet);
    }

    private void saveItWithNewStatus(final DogHasHandler dogHasHandler, final EntityStatus entityStatus) {
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandlerRepository.save(dogHasHandler);
    }

    private Set<DogHasHandler> getActiveDogHasHandlerSet(final Set<UUID> ids) {
        return dogHasHandlerRepository.findAllByIdInAndEntityStatus(ids, EntityStatus.ACTIVE);
    }

    private Optional<casp.web.backend.data.access.layer.dog.DogHasHandler> findMemberAndMapToDogHasHandlerV2(final casp.web.backend.deprecated.dog.DogHasHandler dh, final DogReference dog) {
        return memberReferenceRepository.findById(dh.getMemberId()).map(member -> {
            var dogHasHandler = DOG_HAS_HANDLER_V2_MAPPER.toDogHasHandler(dh);
            dogHasHandler.setDog(dog);
            dogHasHandler.setMember(member);
            return dogHasHandler;
        });
    }
}
