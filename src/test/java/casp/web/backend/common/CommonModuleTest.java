package casp.web.backend.common;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.ApplicationModule;

import static org.junit.jupiter.api.Assertions.assertSame;

class CommonModuleTest {
    private static final String PACKAGE_INFO_REF = "casp.web.backend.common.package-info";

    @Test
    void isOpen() throws ClassNotFoundException {
        var type = Class
                .forName(PACKAGE_INFO_REF)
                .getPackage()
                .getAnnotation(ApplicationModule.class)
                .type();

        assertSame(ApplicationModule.Type.OPEN, type);
    }
}
