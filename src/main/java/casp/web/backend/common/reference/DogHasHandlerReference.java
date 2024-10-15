package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.UUID;

@QueryEntity
@Document(collection = "dogHasHandler")
public class DogHasHandlerReference {
    @Id
    @NotNull
    private UUID id;

    @NotNull
    private EntityStatus entityStatus;

    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    @Valid
    @NotNull
    @DBRef
    private DogReference dog;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(final EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public MemberReference getMember() {
        return member;
    }

    public void setMember(MemberReference member) {
        this.member = member;
    }

    public DogReference getDog() {
        return dog;
    }

    public void setDog(DogReference dog) {
        this.dog = dog;
    }

    public boolean isActive() {
        return entityStatus == EntityStatus.ACTIVE
                && member.getEntityStatus() == EntityStatus.ACTIVE
                && dog.getEntityStatus() == EntityStatus.ACTIVE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerReference that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(member, that.member) && Objects.equals(dog, that.dog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member.getId(), dog.getId());
    }
}
