package casp.web.backend.data.access.layer.dog;

import casp.web.backend.common.BaseDocument;
import casp.web.backend.common.DogReference;
import casp.web.backend.common.MemberReference;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@QueryEntity
@Document
public class DogHasHandler extends BaseDocument {

    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    @Valid
    @NotNull
    @DBRef
    private DogReference dog;

    @NotNull
    @Valid
    private Set<Grade> grades = new HashSet<>();

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

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandler that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(member, that.member) && Objects.equals(dog, that.dog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member.getId(), dog.getId());
    }
}
