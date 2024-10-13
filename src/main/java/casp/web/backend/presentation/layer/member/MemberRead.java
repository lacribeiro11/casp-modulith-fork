package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.member.DogHasHandler;
import casp.web.backend.business.logic.layer.member.MemberDtoRequiredFields;
import casp.web.backend.common.BaseView;
import casp.web.backend.common.EntityStatus;
import casp.web.backend.common.Gender;
import casp.web.backend.common.Role;
import casp.web.backend.data.access.layer.member.Card;
import casp.web.backend.data.access.layer.member.MembershipFee;

import java.time.LocalDate;
import java.util.Set;


class MemberRead extends BaseView implements MemberDtoRequiredFields {
    private EntityStatus entityStatus;
    private Set<DogHasHandler> dogHasHandlerSet;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String telephoneNumber;
    private String email;
    private String address;
    private String postcode;
    private String city;
    private Set<Role> roles;
    private Set<MembershipFee> membershipFees;
    private Set<Card> cards;

    @Override
    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    @Override
    public void setEntityStatus(final EntityStatus entityStatus) {
        this.entityStatus = entityStatus;

    }

    @Override
    public Set<DogHasHandler> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    @Override
    public void setDogHasHandlerSet(final Set<DogHasHandler> dogHasHandlerSet) {
        this.dogHasHandlerSet = dogHasHandlerSet;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(final String lastName) {
        this.lastName = lastName;
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
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    @Override
    public void setTelephoneNumber(final String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(final String address) {
        this.address = address;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Set<MembershipFee> getMembershipFees() {
        return membershipFees;
    }

    @Override
    public void setMembershipFees(final Set<MembershipFee> membershipFees) {
        this.membershipFees = membershipFees;
    }

    @Override
    public Set<Card> getCards() {
        return cards;
    }

    @Override
    public void setCards(final Set<Card> cards) {
        this.cards = cards;
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
