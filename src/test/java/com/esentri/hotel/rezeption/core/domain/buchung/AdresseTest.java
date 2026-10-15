package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdresseTest {

    @Test
    void alleBestandteileSindPflicht() {
        assertThatThrownBy(() -> new Adresse(null, "76131", "Karlsruhe", "Deutschland")).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Adresse("Hauptstrasse 1", " ", "Karlsruhe", "Deutschland")).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Adresse("Hauptstrasse 1", "76131", null, "Deutschland")).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Adresse("Hauptstrasse 1", "76131", "Karlsruhe", "")).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void postleitzahlHatHoechstens10Zeichen() {
        assertThatThrownBy(() -> new Adresse("Hauptstrasse 1", "12345678901", "Karlsruhe", "Deutschland"))
            .isInstanceOf(DomainAssertionException.class);
    }
}
