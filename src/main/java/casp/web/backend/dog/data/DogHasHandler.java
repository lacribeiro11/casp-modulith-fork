package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.DogHasHandlerRequiredFields;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false, of = {"member", "dog"})
//  Compound indexes support lookups by dog or member and entity status.
@CompoundIndex(name = "dog_status", def = "{'dog': 1, 'entityStatus': 1}")
@CompoundIndex(name = "member_status", def = "{'member': 1, 'entityStatus': 1}")
@Getter
@Setter
// The values are derived from the DBRef fields, so the ignore parameters are unnecessary.
@SuppressWarnings({"java:S1172", "unused"})
@QueryEntity
@Document
public class DogHasHandler extends BaseDocument implements DogHasHandlerRequiredFields {
    @NotNull
    @Indexed
    @DocumentReference
    private MemberReference member;
    private String firstName;
    private String lastName;
    @NotNull
    @Indexed
    @DocumentReference
    private DogReference dog;
    private String dogName;
    private String ownerName;
    private Set<Grade> grades = new HashSet<>();

    public String getFirstName() {
        this.firstName = member.getFirstName();
        return firstName;
    }

    public void setFirstName(String ignore) {
        this.firstName = this.member.getFirstName();
    }

    public String getLastName() {
        this.lastName = member.getLastName();
        return lastName;
    }

    public void setLastName(String ignore) {
        this.lastName = member.getLastName();
    }

    public String getDogName() {
        this.dogName = dog.getName();
        return dogName;
    }

    public void setDogName(String ignore) {
        this.dogName = dog.getName();
    }

    public String getOwnerName() {
        this.ownerName = dog.getOwnerName();
        return ownerName;
    }

    public void setOwnerName(String ignore) {
        this.ownerName = dog.getOwnerName();
    }
}
