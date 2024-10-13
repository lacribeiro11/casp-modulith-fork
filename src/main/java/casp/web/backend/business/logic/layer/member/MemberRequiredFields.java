package casp.web.backend.business.logic.layer.member;

import casp.web.backend.common.Gender;
import casp.web.backend.common.Role;
import casp.web.backend.data.access.layer.member.Card;
import casp.web.backend.data.access.layer.member.MembershipFee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public interface MemberRequiredFields {
    @NotBlank
    String getFirstName();

    void setFirstName(@NotBlank String firstName);

    @NotBlank
    String getLastName();

    void setLastName(@NotBlank String lastName);

    LocalDate getBirthDate();

    void setBirthDate(LocalDate birthDate);

    Gender getGender();

    void setGender(Gender gender);

    String getTelephoneNumber();

    void setTelephoneNumber(String telephoneNumber);

    @NotNull
    @Email
    String getEmail();

    void setEmail(@NotNull @Email String email);

    String getAddress();

    void setAddress(String address);

    String getPostcode();

    void setPostcode(String postcode);

    String getCity();

    void setCity(String city);

    @NotEmpty
    Set<Role> getRoles();

    void setRoles(@NotEmpty Set<Role> roles);

    @Valid
    Set<MembershipFee> getMembershipFees();

    void setMembershipFees(@Valid Set<MembershipFee> membershipFees);

    @Valid
    Set<Card> getCards();

    void setCards(@Valid Set<Card> cards);
}
