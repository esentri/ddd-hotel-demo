package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.DomainService;

import java.time.LocalDate;

/**
 * Check-in mit Zimmerzuweisung: Buchung und Zimmer aendern sich gemeinsam in einer Transaktion -
 * die Buchung wird eingecheckt, das Zimmer mit ihr belegt.
 */
public class CheckinService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    public CheckinService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository) {
        this.buchungRepository = buchungRepository;
        this.zimmerRepository = zimmerRepository;
    }

    public Buchung checkeWalkInEin(Gast gast,
                                   Buchungszeitraum buchungszeitraum,
                                   Zimmerkategorie gewuenschteZimmerkategorie,
                                   Zimmer.ZimmerId zimmerId) {
        var zimmer = ladeZimmer(zimmerId);
        var buchung = Buchung.walkIn(gast, buchungszeitraum, gewuenschteZimmerkategorie);
        checkeEin(buchung, zimmer, buchungszeitraum.anreise());
        buchungRepository.insert(buchung);
        zimmerRepository.update(zimmer);
        return buchung;
    }

    private Zimmer ladeZimmer(Zimmer.ZimmerId zimmerId) {
        DomainAssertions.isNotNull(zimmerId, "Beim Check-in muss ein Zimmer zugewiesen werden.");
        var zimmer = zimmerRepository.findById(zimmerId);
        DomainAssertions.isTrue(zimmer.isPresent(), "Das Zimmer ist nicht bekannt.");
        return zimmer.get();
    }

    private static void checkeEin(Buchung buchung, Zimmer zimmer, LocalDate checkinDatum) {
        buchung.checkeEin(checkinDatum, zimmer.getId());
        zimmer.belege(buchung.getId(), buchung.getGewuenschteZimmerkategorie());
    }
}
