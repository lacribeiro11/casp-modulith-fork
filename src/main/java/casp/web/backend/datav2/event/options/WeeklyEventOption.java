package casp.web.backend.datav2.event.options;


import casp.web.backend.common.BaseEventOptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class WeeklyEventOption extends BaseEventOption {
    @Valid
    @NotEmpty
    private List<WeeklyEventOptionRecurrence> occurrences = new ArrayList<>();

    public WeeklyEventOption() {
        super(BaseEventOptionType.WEEKLY);
    }

    public List<WeeklyEventOptionRecurrence> getOccurrences() {
        occurrences.sort(WeeklyEventOptionRecurrence::compareTo);
        return occurrences;
    }

    public void setOccurrences(List<WeeklyEventOptionRecurrence> occurrences) {
        this.occurrences = occurrences;
    }
}
