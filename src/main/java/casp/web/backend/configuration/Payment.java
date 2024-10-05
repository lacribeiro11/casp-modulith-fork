package casp.web.backend.configuration;

import java.time.LocalDate;

public interface Payment {
    double getPaidPrice();

    boolean isPaid();

    LocalDate getPaidDate();
}
