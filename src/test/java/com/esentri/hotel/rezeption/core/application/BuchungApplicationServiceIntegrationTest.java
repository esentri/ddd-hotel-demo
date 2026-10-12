package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierung;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class BuchungApplicationServiceIntegrationTest {

    @Autowired
    BuchungApplicationService buchungApplicationService;

    @Autowired
    BuchungRepository buchungRepository;

    @Test
    void uebernimmtReservierungAlsBuchung() {
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierung("APP-1"));

        assertThat(buchungRepository.findById(buchungsId)).get()
            .extracting(b -> b.getStatus()).isEqualTo(BuchungsStatus.RESERVIERT);
    }

    @Test
    void reservierungWirdNurEinmalUebernommen() {
        buchungApplicationService.uebernimmReservierung(reservierung("APP-2"));

        assertThatThrownBy(() -> buchungApplicationService.uebernimmReservierung(reservierung("APP-2")))
            .isInstanceOf(ReservierungBereitsUebernommenException.class);
    }
}
