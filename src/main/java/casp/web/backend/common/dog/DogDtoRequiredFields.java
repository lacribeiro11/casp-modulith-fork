package casp.web.backend.common.dog;

import jakarta.validation.Valid;

import java.util.Set;

public interface DogDtoRequiredFields extends DogRequiredFields {
    @Valid
    Set<DogHasHandler> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandler> dogHasHandlerSet);
}
