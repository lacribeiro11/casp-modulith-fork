package casp.web.backend.common.validation;

import java.time.LocalDateTime;

public interface CalendarValidation {
    LocalDateTime getEntryFrom();

    LocalDateTime getEntryTo();
}
