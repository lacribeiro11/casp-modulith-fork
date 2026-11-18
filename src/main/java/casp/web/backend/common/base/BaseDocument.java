package casp.web.backend.common.base;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@EqualsAndHashCode(of = "id")
@Setter
@Getter
public abstract class BaseDocument {
    @Id
    protected UUID id = UUID.randomUUID();
    // This field is not required by the Reference classes, but it is ok if it is present.
    @Version
    protected long version;
    @Indexed
    @NotNull
    protected EntityStatus entityStatus = EntityStatus.ACTIVE;
}
