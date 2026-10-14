package com.esentri.hotel;

import io.domainlifecycles.mirror.api.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HotelverwaltungApplicationTests {

    @Test
    void contextLoadsAndDomainMirrorIsInitialized() {
        assertThat(Domain.isInitialized()).isTrue();
    }
}
