package casp.web.backend.configuration;

import java.time.LocalDateTime;

public interface CalendarValidation {
    LocalDateTime getEntryFrom();

    LocalDateTime getEntryTo();
}
