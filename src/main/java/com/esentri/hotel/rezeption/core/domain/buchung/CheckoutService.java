package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.DomainService;

/**
 * Check-out: Buchung und Zimmer aendern sich gemeinsam in einer Transaktion -
 * die Buchung wird ausgecheckt, das zugewiesene Zimmer geraeumt und ist danach zu reinigen.
 */
public class CheckoutService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    public CheckoutService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository) {
        this.buchungRepository = buchungRepository;
        this.zimmerRepository = zimmerRepository;
    }

    public void checkeAus(Buchung.BuchungId buchungId) {
        var buchung = buchungRepository.findById(buchungId);
        DomainAssertions.isTrue(buchung.isPresent(), "Die Buchung ist nicht bekannt.");
        buchung.get().checkeAus();
        var zimmer = zimmerRepository.findById(buchung.get().getZugewiesenesZimmer()).orElseThrow();
        zimmer.raeume(buchungId);
        buchungRepository.update(buchung.get());
        zimmerRepository.update(zimmer);
    }
}
