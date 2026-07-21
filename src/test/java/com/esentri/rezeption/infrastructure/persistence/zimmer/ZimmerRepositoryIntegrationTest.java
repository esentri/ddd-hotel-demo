package com.esentri.rezeption.infrastructure.persistence.zimmer;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ZimmerRepositoryIntegrationTest {

    @Autowired
    private ZimmerRepositoryJooq zimmerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET SCHEMA REZEPTION");
        jdbcTemplate.execute("DELETE FROM zimmer_belegungen");
        jdbcTemplate.execute("DELETE FROM zimmer");
    }


    @Test
    void testInsert() {
        // Arrange
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.EINZELZIMMER);

        // Act
        zimmerRepository.insert(zimmer);

        // Assert
        Optional<Zimmer> found = zimmerRepository.findById(zimmerId);
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(zimmerId);
        assertThat(found.get().getKategorie()).isEqualTo(Zimmerkategorie.EINZELZIMMER);
    }

    @Test
    void testUpdateWithBelegung() {
        // Arrange
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);
        zimmerRepository.insert(zimmer);

        // Act
        Zimmer toUpdate = zimmerRepository.findById(zimmerId).orElseThrow();
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(3));
        toUpdate.belegeFuer(buchungsId, zeitraum);
        zimmerRepository.update(toUpdate);

        // Assert
        Optional<Zimmer> updated = zimmerRepository.findById(zimmerId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getBelegungen()).hasSize(1);
        assertThat(updated.get().getBelegungen().get(0).buchungsId()).isEqualTo(buchungsId);
        assertThat(updated.get().getBelegungen().get(0).zeitraum()).isEqualTo(zeitraum);
    }

    @Test
    void testDelete() {
        // Arrange
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.SUITE);
        zimmerRepository.insert(zimmer);
        assertThat(zimmerRepository.findById(zimmerId)).isPresent();

        // Act
        zimmerRepository.deleteById(zimmerId);

        // Assert
        assertThat(zimmerRepository.findById(zimmerId)).isEmpty();
    }
}
