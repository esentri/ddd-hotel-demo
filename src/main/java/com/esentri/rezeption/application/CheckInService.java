package com.esentri.rezeption.application;

import com.esentri.rezeption.domain.model.buchung.Buchung;
import com.esentri.rezeption.domain.model.buchung.BuchungRepository;
import com.esentri.rezeption.domain.model.buchung.CheckInCommand;
import com.esentri.rezeption.domain.model.buchung.GastEingecheckt;
import com.esentri.rezeption.domain.model.zimmer.Zimmer;
import com.esentri.rezeption.domain.model.zimmer.ZimmerRepository;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInService implements ApplicationService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    @Transactional
    @Publishes(domainEventTypes = GastEingecheckt.class)
    public void checkIn(CheckInCommand command) {
        Buchung buchung = buchungRepository.findById(command.buchungId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung nicht gefunden: " + command.buchungId()));

        Zimmer zimmer = zimmerRepository.findById(command.zimmerId())
            .orElseThrow(() -> new IllegalArgumentException("Zimmer nicht gefunden: " + command.zimmerId()));

        // 1. Buchung aktualisieren (prüft Invarianten wie Alter)
        buchung.checkIn(zimmer.id());

        // 2. Zimmer belegen (prüft Doppelbelegung)
        zimmer.fügeBelegungHinzu(buchung.getZeitraum(), buchung.id());

        // 3. Persistieren
        buchungRepository.update(buchung);
        zimmerRepository.update(zimmer);

        // 4. Event veröffentlichen
        DomainEvents.publish(new GastEingecheckt(buchung.id(), zimmer.id()));
    }
}
