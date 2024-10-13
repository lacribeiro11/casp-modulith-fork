package casp.web.backend.business.logic.layer.member;

import casp.web.backend.common.EntityStatus;
import jakarta.validation.Valid;

import java.util.Set;

public interface MemberDtoRequiredFields extends MemberRequiredFields {
    EntityStatus getEntityStatus();

    void setEntityStatus(EntityStatus entityStatus);

    @Valid
    Set<DogHasHandler> getDogHasHandlerSet();

    void setDogHasHandlerSet(@Valid Set<DogHasHandler> dogHasHandlerSet);
}
