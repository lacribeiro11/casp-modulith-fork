package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogDto;
import casp.web.backend.business.logic.layer.dog.DogService;
import casp.web.backend.business.logic.layer.dog.EuropeNetTasks;
import casp.web.backend.data.access.layer.dog.Dog;
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
    ResponseEntity<DogDto> getDogById(final @PathVariable UUID id) {
        return ResponseEntity.ok(dogService.getDogById(id));
    }

    @GetMapping
    ResponseEntity<Page<Dog>> getDogs(final @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(dogService.getDogs(pageable));
    }

    @PostMapping
    ResponseEntity<DogDto> saveDog(final @RequestBody @Valid Dog dog) {
        return ResponseEntity.ok(dogService.saveDog(dog));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteDogById(final @PathVariable UUID id) {
        dogService.deleteDogById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("register")
    ResponseEntity<Page<Dog>> register(final @ParameterObject @Nullable Pageable pageable) {
        return ResponseEntity.ok(europeNetTasks.registerDogsManually(pageable));
    }

    @GetMapping("by-chip-number/{chipNumber}")
    ResponseEntity<Dog> getDogByChipNumber(final @PathVariable String chipNumber) {
        return ResponseEntity.of(dogService.getDogByChipNumber(chipNumber));
    }

    @GetMapping("by-dog-name-or-owner-name")
    ResponseEntity<Page<Dog>> getDogsByNameOrOwnerName(final @RequestParam(required = false) String name,
                                                       final @RequestParam(required = false) String ownerName,
                                                       final @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(dogService.getDogsByNameOrOwnerName(name, ownerName, pageable));
    }
}
