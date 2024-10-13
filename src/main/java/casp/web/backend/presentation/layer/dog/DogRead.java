package casp.web.backend.presentation.layer.dog;

import casp.web.backend.common.BaseView;
import casp.web.backend.common.EuropeNetState;
import casp.web.backend.common.Gender;
import casp.web.backend.common.dog.DogDtoRequiredFields;
import casp.web.backend.common.dog.DogHasHandler;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DogRead extends BaseView implements DogDtoRequiredFields {
    private String name;
    private String breederName;
    private String breedName;
    private LocalDate birthDate;
    private String pedigree;
    private Gender gender = Gender.FEMALE;
    private String chipNumber;
    private LocalDate rabiesDate;
    private float height;
    private String ownerName;
    private String ownerAddress;
    private EuropeNetState europeNetState = EuropeNetState.NOT_CHECKED;
    private Set<DogHasHandler> dogHasHandlerSet = new HashSet<>();

    @Override
    public Set<DogHasHandler> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    @Override
    public void setDogHasHandlerSet(final Set<DogHasHandler> dogHasHandlerSet) {
        this.dogHasHandlerSet = dogHasHandlerSet;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getBreederName() {
        return breederName;
    }

    @Override
    public void setBreederName(final String breederName) {
        this.breederName = breederName;
    }

    @Override
    public String getBreedName() {
        return breedName;
    }

    @Override
    public void setBreedName(final String breedName) {
        this.breedName = breedName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String getPedigree() {
        return pedigree;
    }

    @Override
    public void setPedigree(final String pedigree) {
        this.pedigree = pedigree;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getChipNumber() {
        return chipNumber;
    }

    @Override
    public void setChipNumber(final String chipNumber) {
        this.chipNumber = chipNumber;
    }

    @Override
    public LocalDate getRabiesDate() {
        return rabiesDate;
    }

    @Override
    public void setRabiesDate(final LocalDate rabiesDate) {
        this.rabiesDate = rabiesDate;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(final float height) {
        this.height = height;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getOwnerAddress() {
        return ownerAddress;
    }

    @Override
    public void setOwnerAddress(final String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    @Override
    public EuropeNetState getEuropeNetState() {
        return europeNetState;
    }

    @Override
    public void setEuropeNetState(final EuropeNetState europeNetState) {
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
