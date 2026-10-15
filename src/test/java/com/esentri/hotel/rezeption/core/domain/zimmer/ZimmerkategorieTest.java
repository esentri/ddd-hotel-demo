package com.esentri.hotel.rezeption.core.domain.zimmer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZimmerkategorieTest {

    @Test
    void kategorienSindAufsteigendGeordnet() {
        assertThat(Zimmerkategorie.SUITE.istGleichOderHoeherAls(Zimmerkategorie.DOPPELZIMMER)).isTrue();
        assertThat(Zimmerkategorie.DOPPELZIMMER.istGleichOderHoeherAls(Zimmerkategorie.DOPPELZIMMER)).isTrue();
        assertThat(Zimmerkategorie.EINZELZIMMER.istGleichOderHoeherAls(Zimmerkategorie.DOPPELZIMMER)).isFalse();
    }

    @Test
    void gleichOderHoeher() {
        assertThat(Zimmerkategorie.DOPPELZIMMER.gleichOderHoeher())
            .containsExactly(Zimmerkategorie.DOPPELZIMMER, Zimmerkategorie.SUITE);
        assertThat(Zimmerkategorie.EINZELZIMMER.gleichOderHoeher()).containsExactly(Zimmerkategorie.values());
    }
}
