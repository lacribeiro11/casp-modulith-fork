package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.business.logic.layer.dog.EuropeNetTasks;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static casp.web.backend.presentation.layer.dog.DogReadMapper.READ_MAPPER;
import static casp.web.backend.presentation.layer.dog.DogWriteMapper.WRITE_MAPPER;

@RestController
@RequestMapping("dog")
@Validated
class DogRestController {

    private final DogService dogService;
    private final EuropeNetTasks europeNetTasks;

    @Autowired
    DogRestController(final DogService dogService, final EuropeNetTasks europeNetTasks) {
        this.dogService = dogService;
        this.europeNetTasks = europeNetTasks;
    }

    @GetMapping("{id}")
    ResponseEntity<DogRead> getDogById(final @PathVariable UUID id) {
        var dogDto = dogService.getDogById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogDto));
    }

    @GetMapping
    ResponseEntity<Page<DogRead>> getDogs(final @ParameterObject Pageable pageable) {
        var dogDtoPage = dogService.getDogs(pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }

    @PostMapping
    ResponseEntity<DogRead> saveDog(final @RequestBody @Valid DogWrite dogWrite) {
        var dogDto = dogService.saveDog(WRITE_MAPPER.toSource(dogWrite));
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteDogById(final @PathVariable UUID id) {
        dogService.deleteDogById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("register")
    ResponseEntity<Page<DogRead>> register(final @ParameterObject @Nullable Pageable pageable) {
        var dogDtoPage = europeNetTasks.registerDogsManually(pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }

    @GetMapping("by-chip-number/{chipNumber}")
    ResponseEntity<DogRead> getDogByChipNumber(final @PathVariable String chipNumber) {
        return ResponseEntity.of(dogService.getDogByChipNumber(chipNumber)
                .map(READ_MAPPER::toTarget));
    }

    @GetMapping("by-dog-name-or-owner-name")
    ResponseEntity<Page<DogRead>> getDogsByNameOrOwnerName(final @RequestParam(required = false) String name,
                                                           final @RequestParam(required = false) String ownerName,
                                                           final @ParameterObject Pageable pageable) {
        var dogDtoPage = dogService.getDogsByNameOrOwnerName(name, ownerName, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogDtoPage));
    }
}
