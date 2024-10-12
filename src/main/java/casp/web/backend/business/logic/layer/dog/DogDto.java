package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;

import java.util.HashSet;
import java.util.Set;

public class DogDto extends Dog {
    private Set<DogHasHandlerDto> dogHasHandlerSet = new HashSet<>();

    public Set<DogHasHandlerDto> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    public void setDogHasHandlerSet(final Set<DogHasHandlerDto> dogHasHandlerSet) {
        this.dogHasHandlerSet = dogHasHandlerSet;
    }
}
