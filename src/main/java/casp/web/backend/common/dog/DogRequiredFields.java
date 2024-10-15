package casp.web.backend.common.dog;

import casp.web.backend.common.enums.EuropeNetState;
import casp.web.backend.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public interface DogRequiredFields {
    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getBreederName();

    void setBreederName(String breederName);

    String getBreedName();

    void setBreedName(String breedName);

    LocalDate getBirthDate();

    void setBirthDate(LocalDate birthDate);

    String getPedigree();

    void setPedigree(String pedigree);

    @NotNull
    Gender getGender();

    void setGender(@NotNull Gender gender);

    String getChipNumber();

    void setChipNumber(String chipNumber);

    LocalDate getRabiesDate();

    void setRabiesDate(LocalDate rabiesDate);

    float getHeight();

    void setHeight(float height);

    @NotBlank
    String getOwnerName();

    void setOwnerName(@NotBlank String ownerName);

    @NotBlank
    String getOwnerAddress();

    void setOwnerAddress(@NotBlank String ownerAddress);

    @NotNull
    EuropeNetState getEuropeNetState();

    void setEuropeNetState(@NotNull EuropeNetState europeNetState);
}
