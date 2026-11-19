package casp.web.backend;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

@Slf4j
class ModularityTest {
    @Test
    void verifiesModuleBoundaries() {
        var modules = ApplicationModules.of(AdminApplication.class);

        modules.forEach(m -> log.info("{}", m));
        modules.verify();
    }
}
