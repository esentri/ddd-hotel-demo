package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.Factory;

import java.util.UUID;

/**
 * Legt neue Zimmer an. Die Zimmernummer ist im Hotel eindeutig; ein neues Zimmer ist bezugsfertig.
 */
public class ZimmerFactory implements Factory {

    private final ZimmerRepository zimmerRepository;

    public ZimmerFactory(ZimmerRepository zimmerRepository) {
        this.zimmerRepository = zimmerRepository;
    }

    public Zimmer legeAn(LegeZimmerAn command) {
        DomainAssertions.isTrue(zimmerRepository.findByZimmernummer(command.zimmernummer()).isEmpty(),
            "Ein Zimmer mit der Nummer " + command.zimmernummer().value() + " gibt es bereits.");
        return new Zimmer(
            0L,
            new Zimmer.ZimmerId(UUID.randomUUID()),
            command.zimmernummer(),
            command.zimmerkategorie(),
            command.etage(),
            command.blickrichtung(),
            command.balkon(),
            command.barrierefrei(),
            command.bettart(),
            command.raucherzimmer(),
            Zimmerstatus.BEZUGSFERTIG,
            null);
    }
}
