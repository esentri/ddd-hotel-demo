package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.buchung.AktualisiereGastdaten;
import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import io.domainlifecycles.domain.types.ApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public class BuchungApplicationService implements ApplicationService {

    private final BuchungRepository buchungRepository;

    public BuchungApplicationService(BuchungRepository buchungRepository) {
        this.buchungRepository = Objects.requireNonNull(buchungRepository, "BuchungRepository darf nicht null sein");
    }

    @Transactional
    public BuchungsId aktualisiereGastdaten(AktualisiereGastdaten command) {
        Objects.requireNonNull(command, "Command darf nicht null sein");

        Buchung buchung = buchungRepository.findById(command.buchungsId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung mit ID " + command.buchungsId() + " nicht gefunden"));

        buchung.aktualisiereGastdaten(
            command.vorname(),
            command.nachname(),
            command.geburtsdatum()
        );

        buchungRepository.update(buchung);

        return buchung.id();
    }
}
