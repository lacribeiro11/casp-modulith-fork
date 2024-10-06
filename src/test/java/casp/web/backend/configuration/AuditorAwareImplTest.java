package casp.web.backend.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;

import static org.assertj.core.api.Assertions.assertThat;


class AuditorAwareImplTest {
    private static final AuditorAware<String> AUDITOR_AWARE = new AuditorAwareImpl();

    @Test
    void getCurrentAuditor() {
        assertThat(AUDITOR_AWARE.getCurrentAuditor()).contains("Bonsai");
    }
}
