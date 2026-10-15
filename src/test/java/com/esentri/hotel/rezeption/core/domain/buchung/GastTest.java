package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GastTest {

    @Test
    void vornameAdresseUndGeburtsdatumSindOptional() {
        assertThat(Gast.neu(null, "Mustermann", null, null).getNachname()).isEqualTo("Mustermann");
    }

    @Test
    void nachnameIstPflicht() {
        assertThatThrownBy(() -> Gast.neu("Erika", null, null, null)).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> Gast.neu("Erika", " ", null, null)).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void nameHatHoechstens100Zeichen() {
        assertThatThrownBy(() -> Gast.neu("Erika", "x".repeat(101), null, null)).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void geburtsdatumDarfNichtInDerZukunftLiegen() {
        assertThatThrownBy(() -> Gast.neu("Erika", "Mustermann", null, LocalDate.now().plusDays(1)))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void alterInVollendetenLebensjahren() {
        var gast = Gast.neu("Erika", "Mustermann", null, LocalDate.of(2010, 10, 16));

        assertThat(gast.alterAm(LocalDate.of(2026, 10, 15))).isEqualTo(15);
        assertThat(gast.alterAm(LocalDate.of(2026, 10, 16))).isEqualTo(16);
    }
}
