package casp.web.backend.common.validation;

import java.time.LocalDate;

public interface BaseEventOptionValidation {
    LocalDate getStartRecurrence();

    LocalDate getEndRecurrence();
}
