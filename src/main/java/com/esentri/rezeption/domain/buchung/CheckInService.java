package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
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

    public void weiseZimmerZuUndCheckeEin(BuchungsId buchungsId, ZimmerId zimmerId) {
        Buchung buchung = buchungRepository.findById(buchungsId)
            .orElseThrow(() -> new IllegalArgumentException("Buchung nicht gefunden: " + buchungsId));
        Zimmer zimmer = zimmerRepository.findById(zimmerId)
            .orElseThrow(() -> new IllegalArgumentException("Zimmer nicht gefunden: " + zimmerId));

        if (!zimmer.istVerfuegbarFuer(buchung.getBelegungszeitraum())) {
            throw new ZimmerNichtVerfuegbarException("Das Zimmer " + zimmerId + " ist nicht verfuegbar.");
        }

        zimmer.belegeFuer(buchung.id(), buchung.getBelegungszeitraum());
        buchung.checkeEin(zimmer.id());

        buchungRepository.update(buchung);
        zimmerRepository.update(zimmer);
    }
}
