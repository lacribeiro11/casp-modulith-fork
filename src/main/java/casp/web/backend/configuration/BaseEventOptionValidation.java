package casp.web.backend.configuration;

import java.time.LocalDate;

public interface BaseEventOptionValidation {
    LocalDate getStartRecurrence();

    LocalDate getEndRecurrence();
}
