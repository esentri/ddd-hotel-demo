package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungszeitraumTest {

    private static final LocalDate HEUTE = LocalDate.of(2026, 10, 15);

    @Test
    void abreiseMussNachAnreiseLiegen() {
        assertThatThrownBy(() -> new Buchungszeitraum(HEUTE, HEUTE))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Abreise");
        assertThatThrownBy(() -> new Buchungszeitraum(HEUTE, HEUTE.minusDays(1)))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void anreiseUndAbreiseSindPflicht() {
        assertThatThrownBy(() -> new Buchungszeitraum(null, HEUTE)).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Buchungszeitraum(HEUTE, null)).isInstanceOf(DomainAssertionException.class);
    }
}
