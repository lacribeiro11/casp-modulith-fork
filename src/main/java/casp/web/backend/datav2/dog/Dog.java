package casp.web.backend.datav2.dog;

import casp.web.backend.common.BaseDocument;
import casp.web.backend.common.EuropeNetState;
import casp.web.backend.common.Gender;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@QueryEntity
@Document
public class Dog extends BaseDocument {

    @NotBlank
    private String name;

    private String breederName;

    private String breedName;

    private LocalDate birthDate;

    private String pedigree;

    @NotNull
    private Gender gender = Gender.FEMALE;

    private String chipNumber;

    private LocalDate rabiesDate;

    private float height;

    @NotBlank
    private String ownerName;

    @NotBlank
    private String ownerAddress;

    @NotNull
    private EuropeNetState europeNetState = EuropeNetState.NOT_CHECKED;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreederName() {
        return breederName;
    }

    public void setBreederName(String breederName) {
        this.breederName = breederName;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPedigree() {
        return pedigree;
    }

    public void setPedigree(String pedigree) {
        this.pedigree = pedigree;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getChipNumber() {
        return chipNumber;
    }

    public void setChipNumber(String chipNumber) {
        this.chipNumber = chipNumber;
    }

    public LocalDate getRabiesDate() {
        return rabiesDate;
    }

    public void setRabiesDate(LocalDate rabiesDate) {
        this.rabiesDate = rabiesDate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public EuropeNetState getEuropeNetState() {
        return europeNetState;
    }

    public void setEuropeNetState(EuropeNetState europeNetState) {
        this.europeNetState = europeNetState;
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
