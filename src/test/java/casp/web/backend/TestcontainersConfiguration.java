package casp.web.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer("mongo:latest")
                // TODO Linux kernel versions 6.19 and newer has a known incompatibility with this version of MongoDB.
                .withEnv("GLIBC_TUNABLES", "glibc.pthread.rseq=1");
    }
}
