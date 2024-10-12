package casp.web.backend.business.logic.layer.member;

import casp.web.backend.data.access.layer.member.Member;

import java.util.HashSet;
import java.util.Set;

public class MemberDto extends Member {
    private Set<DogHasHandlerDto> dogHasHandlerSet = new HashSet<>();

    public Set<DogHasHandlerDto> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    public void setDogHasHandlerSet(final Set<DogHasHandlerDto> dogHasHandlerSet) {
        this.dogHasHandlerSet = dogHasHandlerSet;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
