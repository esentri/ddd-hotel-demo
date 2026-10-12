package com.esentri.rezeption.infrastructure.persistence.auslastung;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.auslastung.Zimmerauslastung;
import com.esentri.rezeption.domain.auslastung.ZimmerauslastungQueryHandler;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ZimmerauslastungQueryHandlerIntegrationTest {

    private static final LocalDate START = LocalDate.of(2030, 5, 1);

    @Autowired
    private ZimmerauslastungQueryHandler queryHandler;

    @Autowired
    private ZimmerRepository zimmerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM REZEPTION.ZIMMER_BELEGUNGEN");
        jdbcTemplate.execute("DELETE FROM REZEPTION.ZIMMER");

        Zimmer belegt = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        belegt.belegeFuer(new BuchungsId(UUID.randomUUID()), new Zeitraum(START, START.plusDays(1)));
        zimmerRepository.insert(belegt);
        zimmerRepository.insert(new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER));
        zimmerRepository.insert(new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.SUITE));
    }

    @Test
    void Should_ReturnTheOccupancyPerDayAndCategory_OrderedByDayAndCategory() {
        // when
        List<Zimmerauslastung> auslastung = queryHandler.findeZimmerauslastung(START, START.plusDays(1), null);

        // then
        assertThat(auslastung).containsExactly(
            new Zimmerauslastung(START, Zimmerkategorie.EINZELZIMMER, 1, 2),
            new Zimmerauslastung(START, Zimmerkategorie.SUITE, 0, 1),
            new Zimmerauslastung(START.plusDays(1), Zimmerkategorie.EINZELZIMMER, 1, 2),
            new Zimmerauslastung(START.plusDays(1), Zimmerkategorie.SUITE, 0, 1));
    }

    @Test
    void Should_ReturnOnlyTheRequestedCategory_When_OneIsGiven() {
        // when
        List<Zimmerauslastung> auslastung =
            queryHandler.findeZimmerauslastung(START, START, Zimmerkategorie.SUITE);

        // then
        assertThat(auslastung).containsExactly(new Zimmerauslastung(START, Zimmerkategorie.SUITE, 0, 1));
    }
}
