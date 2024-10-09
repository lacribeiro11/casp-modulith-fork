package casp.web.backend.data.access.layer.event.options;

import casp.web.backend.common.BaseEventOptionType;
import casp.web.backend.configuration.BaseEventOptionRecurrencesConstraint;
import casp.web.backend.configuration.BaseEventOptionValidation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@BaseEventOptionRecurrencesConstraint
public abstract class BaseEventOption implements BaseEventOptionValidation {
    @NotNull
    BaseEventOptionType optionType;

    @NotNull
    LocalDate startRecurrence;

    @NotNull
    LocalDate endRecurrence;

    @Positive
    int repeatEvery = 1;

    BaseEventOption(final BaseEventOptionType optionType) {
        this.optionType = optionType;
    }

    @Override
    public LocalDate getStartRecurrence() {
        return startRecurrence;
    }

    public void setStartRecurrence(LocalDate startRecurrence) {
        this.startRecurrence = startRecurrence;
    }

    @Override
    public LocalDate getEndRecurrence() {
        return endRecurrence;
    }

    public void setEndRecurrence(LocalDate endRecurrence) {
        this.endRecurrence = endRecurrence;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(int repeatEvery) {
        this.repeatEvery = repeatEvery;
    }

    public BaseEventOptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(final BaseEventOptionType optionType) {
        this.optionType = optionType;
    }
}
