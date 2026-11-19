package casp.web.backend.common.reference;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@EqualsAndHashCode(callSuper = false, of = {"member", "dog"})
@Setter
@Getter
@QueryEntity
@Document(collection = "dogHasHandler")
public class DogHasHandlerReference extends BaseDocument {
    @NotNull
    @Indexed
    @DocumentReference
    private MemberReference member;
    @NotNull
    @Indexed
    @DocumentReference
    private DogReference dog;

    public boolean isActive() {
        return entityStatus == EntityStatus.ACTIVE
                && member.getEntityStatus() == EntityStatus.ACTIVE
                && dog.getEntityStatus() == EntityStatus.ACTIVE;
    }
}
