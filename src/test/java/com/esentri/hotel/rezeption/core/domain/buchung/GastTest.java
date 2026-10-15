package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GastTest {

    @Test
    void vornameIstOptional() {
        assertThat(Gast.neu(null, "Mustermann").getNachname()).isEqualTo("Mustermann");
    }

    @Test
    void nachnameIstPflicht() {
        assertThatThrownBy(() -> Gast.neu("Erika", null)).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> Gast.neu("Erika", " ")).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void nameHatHoechstens100Zeichen() {
        assertThatThrownBy(() -> Gast.neu("Erika", "x".repeat(101))).isInstanceOf(DomainAssertionException.class);
    }
}
