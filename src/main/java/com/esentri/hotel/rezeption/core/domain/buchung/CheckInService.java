package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;

import java.time.LocalDate;

/**
 * Beim CheckIn aendern sich Buchung und Zimmer gemeinsam: die Buchung bekommt das Zimmer zugewiesen und das Zimmer
 * wird fuer den gesamten Aufenthalt durch die Buchung belegt. Das Zimmer wird immer im Hotel der Buchung ueber seine Zimmernummer gesucht.
 */
public class CheckInService implements DomainService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;

    public CheckInService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository) {
        this.buchungRepository = buchungRepository;
        this.zimmerRepository = zimmerRepository;
    }

    @Publishes(domainEventTypes = GastEingecheckt.class)
    public Buchung checkeEin(CheckeEin checkIn, LocalDate heute) {
        var buchung = buchungRepository.findById(checkIn.buchungsId())
            .orElseThrow(() -> new NichtGefundenException(
                "Die Buchung %s existiert nicht.".formatted(checkIn.buchungsId().value())));
        var zimmer = zimmerImHotel(buchung.getHotelId(), checkIn.zimmernummer());

        buchung.checkeEin(zimmer.id(), zimmer.getKategorie(), checkIn.gastdaten(), heute);
        zimmer.belegeDurchGast(buchung.id(), buchung.getHotelId(), belegungszeitraum(buchung));

        buchungRepository.update(buchung);
        zimmerRepository.update(zimmer);
        DomainEvents.publish(new GastEingecheckt(buchung.id(), buchung.getHotelId(), zimmer.id()));
        return buchung;
    }

    @Publishes(domainEventTypes = GastEingecheckt.class)
    public Buchung checkeSpontanEin(CheckeSpontanEin checkIn, LocalDate heute) {
        var zimmer = zimmerImHotel(checkIn.hotelId(), checkIn.zimmernummer());

        var buchung = Buchung.checkeSpontanEin(checkIn.hotelId(), checkIn.gastdaten(),
            checkIn.aufenthaltszeitraum(), zimmer.id(), zimmer.getKategorie(), heute);
        zimmer.belegeDurchGast(buchung.id(), buchung.getHotelId(), belegungszeitraum(buchung));

        buchungRepository.insert(buchung);
        zimmerRepository.update(zimmer);
        DomainEvents.publish(new GastEingecheckt(buchung.id(), buchung.getHotelId(), zimmer.id()));
        return buchung;
    }

    private static Belegungszeitraum belegungszeitraum(Buchung buchung) {
        var aufenthalt = buchung.getAufenthaltszeitraum();
        return new Belegungszeitraum(aufenthalt.anreise(), aufenthalt.abreise());
    }

    private Zimmer zimmerImHotel(HotelId hotelId, Zimmernummer zimmernummer) {
        return zimmerRepository.findByHotelUndZimmernummer(hotelId, zimmernummer)
            .orElseThrow(() -> new NichtGefundenException(
                "Das Hotel hat kein Zimmer mit der Nummer %s.".formatted(zimmernummer.value())));
    }
}
