package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Beim Checkout aendern sich Buchung und Zimmer gemeinsam: die Buchung wird ausgecheckt, die Belegung des Zimmers
 * endet (bei vorzeitiger Abreise heute).
 */
public class CheckOutService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    public CheckOutService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository) {
        this.buchungRepository = buchungRepository;
        this.zimmerRepository = zimmerRepository;
    }

    @Publishes(domainEventTypes = GastAusgecheckt.class)
    public Buchung checkeAus(CheckeAus checkOut, LocalDate heute) {
        var zimmer = zimmerRepository.findByHotelUndZimmernummer(checkOut.hotelId(), checkOut.zimmernummer())
            .orElseThrow(() -> new NichtGefundenException(
                "Das Hotel hat kein Zimmer mit der Nummer %s.".formatted(checkOut.zimmernummer().value())));
        // Ist der Vorgaenger noch nicht ausgecheckt, reist zuerst der mit der fruehesten Anreise ab.
        var buchung = buchungRepository.findEingecheckteBuchungen(zimmer.id()).stream()
            .min(Comparator.comparing(b -> b.getAufenthaltszeitraum().anreise()))
            .orElseThrow(() -> new NichtGefundenException(
                "Im Zimmer %s ist kein Gast eingecheckt.".formatted(checkOut.zimmernummer().value())));

        buchung.checkeAus(heute);
        zimmer.beendeGastBelegung(buchung.id(), heute);

        buchungRepository.update(buchung);
        zimmerRepository.update(zimmer);
        DomainEvents.publish(new GastAusgecheckt(buchung.id(), buchung.getHotelId(), zimmer.id(), heute));
        return buchung;
    }
}
