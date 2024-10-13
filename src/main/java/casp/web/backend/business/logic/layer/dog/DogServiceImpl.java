package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.common.DogHasHandlerReferenceRepository;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static casp.web.backend.business.logic.layer.dog.DogMapper.DOG_MAPPER;

@Service
class DogServiceImpl implements DogService {
    private static final Logger LOG = LoggerFactory.getLogger(DogServiceImpl.class);

    private final DogRepository dogRepository;
    private final DogHasHandlerService dogHasHandlerService;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    DogServiceImpl(final DogRepository dogRepository,
                   final DogHasHandlerService dogHasHandlerService,
                   final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.dogRepository = dogRepository;
        this.dogHasHandlerService = dogHasHandlerService;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    @Override
    public DogDto getDogById(final UUID id) {
        var dogDto = DOG_MAPPER.toTarget(getActiveDog(id));
        var dogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(id);
        dogDto.setDogHasHandlerSet(DOG_MAPPER.toDogHasHandlerDtoSet(dogHasHandlerSet));
        return dogDto;
    }

    @Override
    public DogDto saveDog(final Dog dog) {
        dogRepository.save(dog);
        return getDogById(dog.getId());
    }

    @Override
    public void deleteDogById(final UUID id) {
        var dog = getActiveDog(id);
        dogHasHandlerService.deleteDogHasHandlersByDogId(id);
        dog.setEntityStatus(EntityStatus.DELETED);
        dogRepository.save(dog);
    }

    @Override
    public Optional<Dog> getDogByChipNumber(final String chipNumber) {
        return dogRepository.findDogByChipNumberAndEntityStatus(chipNumber, EntityStatus.ACTIVE);
    }

    @Override
    public Page<Dog> getDogsByNameOrOwnerName(final String name, final String ownerName, final Pageable pageable) {
        return dogRepository.findAllByNameOrOwnerName(name, ownerName, pageable);
    }

    @Override
    public Page<Dog> getDogs(final Pageable pageable) {
        return dogRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
    }

    @Override
    public Page<Dog> getDogsThatWereNotChecked(final Pageable pageable) {
        var pageRequest = pageable != null ? pageable : Pageable.unpaged();
        return dogRepository.findAllByEuropeNetStateNotChecked(pageRequest);
    }

    private Dog getActiveDog(final UUID id) {
        return dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Dog with id %s not found or it isn't active.".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }
}
