package casp.web.backend.presentation.layer.dog;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dog.DogHasHandlerReadMapper.READ_MAPPER;
import static casp.web.backend.presentation.layer.dog.DogHasHandlerWriteMapper.WRITE_MAPPER;


@RestController
@RequestMapping("dog-has-handler")
@Validated
class DogHasHandlerRestController {

    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    DogHasHandlerRestController(final DogHasHandlerService dogHasHandlerService) {
        this.dogHasHandlerService = dogHasHandlerService;
    }

    @GetMapping("{id}")
    ResponseEntity<DogHasHandlerRead> getDogHasHandlerById(final @PathVariable UUID id) {
        var dogHasHandlerDto = dogHasHandlerService.getDogHasHandlerById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogHasHandlerDto));
    }


    @PostMapping
    ResponseEntity<DogHasHandlerRead> saveDogHasHandler(final @RequestBody @Valid DogHasHandlerWrite dogHasHandlerWrite) {
        var dogHasHandlerDto = WRITE_MAPPER.toSource(dogHasHandlerWrite);
        dogHasHandlerDto = dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto);
        return ResponseEntity.ok(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteDogHasHandlerById(final @PathVariable UUID id) {
        dogHasHandlerService.deleteDogHasHandlerById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("search-by-name")
    ResponseEntity<Page<DogHasHandlerRead>> searchByName(final @RequestParam(required = false, defaultValue = "") String name, final @ParameterObject Pageable pageable) {
        var dogHasHandlerDtoPage = dogHasHandlerService.searchByName(name, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogHasHandlerDtoPage));
    }

    @GetMapping
    ResponseEntity<Page<DogHasHandlerRead>> getAllDogHasHandlers(final @ParameterObject Pageable pageable) {
        var dogHasHandlerDtoPage = dogHasHandlerService.getAllDogHasHandlers(pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(dogHasHandlerDtoPage));
    }

    @GetMapping("by-ids")
    ResponseEntity<Set<DogHasHandlerRead>> getDogHasHandlersByHandlerIds(final @RequestParam @Size(min = 1) Set<UUID> ids) {
        var dogHasHandlerDtoSet = dogHasHandlerService.getDogHasHandlersByIds(ids);
        return ResponseEntity.ok(READ_MAPPER.toTargetSet(dogHasHandlerDtoSet));
    }

    @GetMapping("emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(final @RequestParam @Size(min = 1) Set<UUID> ids) {
        return ResponseEntity.ok(dogHasHandlerService.getEmailsByDogHasHandlersIds(ids));
    }

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    @PostMapping("migrate-data")
    ResponseEntity<Void> migrateDataToV2() {
        dogHasHandlerService.migrateDataToV2();
        return ResponseEntity.noContent().build();
    }
}
