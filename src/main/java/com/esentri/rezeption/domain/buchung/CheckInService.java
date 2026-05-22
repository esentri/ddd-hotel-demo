package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerNichtVerfuegbarException;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.domain.types.DomainService;

import java.util.Objects;

public class CheckInService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    public CheckInService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository) {
        this.buchungRepository = Objects.requireNonNull(buchungRepository);
        this.zimmerRepository = Objects.requireNonNull(zimmerRepository);
    }

    public void weiseZimmerZuUndCheckeEin(CheckeGastEin command) {
        Buchung buchung = buchungRepository.findById(command.buchungsId())
            .orElseThrow(() -> new IllegalArgumentException("Buchung nicht gefunden: " + command.buchungsId()));
        Zimmer zimmer = zimmerRepository.findById(command.zimmerId())
            .orElseThrow(() -> new IllegalArgumentException("Zimmer nicht gefunden: " + command.zimmerId()));

        if (!zimmer.istVerfuegbarFuer(buchung.getBelegungszeitraum())) {
            throw new ZimmerNichtVerfuegbarException("Das Zimmer " + command.zimmerId() + " ist nicht verfuegbar.");
        }

        zimmer.belegeFuer(buchung.id(), buchung.getBelegungszeitraum());
        buchung.checkeEin(zimmer.id());

        buchungRepository.update(buchung);
        zimmerRepository.update(zimmer);
    }
}
