package casp.web.backend.common.reference;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.Gender;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Getter
@QueryEntity
@Document(collection = "dog")
public class DogReference extends BaseDocument {
    @NotBlank
    private String name;
    @NotBlank
    private String ownerName;
    private String breederName;
    private String breedName;
    private LocalDate birthDate;
    private Gender gender = Gender.FEMALE;
}
