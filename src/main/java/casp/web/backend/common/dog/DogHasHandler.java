package casp.web.backend.common.dog;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DogHasHandler {
    @NotNull
    private UUID id;
    @NotNull
    private UUID memberId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(final UUID memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandler that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
