package casp.web.backend.common.validation;

import java.time.LocalDate;

public interface Payment {
    double getPaidPrice();

    boolean isPaid();

    LocalDate getPaidDate();
}
