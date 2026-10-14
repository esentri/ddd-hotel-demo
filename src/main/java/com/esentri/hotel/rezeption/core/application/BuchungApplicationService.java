package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungStorniert;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckInService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeSpontanEin;
import com.esentri.hotel.rezeption.core.domain.buchung.GastAusgecheckt;
import com.esentri.hotel.rezeption.core.domain.buchung.ReservierungUebernommen;
import com.esentri.hotel.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.hotel.rezeption.core.domain.buchung.UebernimmReservierung;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;

public class BuchungApplicationService implements ApplicationService {

    private final BuchungRepository buchungRepository;
    private final ZimmerRepository zimmerRepository;
    private final CheckInService checkInService;
    private final Clock clock;

    public BuchungApplicationService(BuchungRepository buchungRepository, ZimmerRepository zimmerRepository,
                                     CheckInService checkInService, Clock clock) {
        this.buchungRepository = buchungRepository;
        this.zimmerRepository = zimmerRepository;
        this.checkInService = checkInService;
        this.clock = clock;
    }

    @Transactional
    @Publishes(domainEventTypes = ReservierungUebernommen.class)
    public Buchung.BuchungsId uebernimmReservierung(UebernimmReservierung reservierung) {
        if (buchungRepository.findByReservierungsnummer(reservierung.reservierungsnummer()).isPresent()) {
            throw new ReservierungBereitsUebernommenException(reservierung.reservierungsnummer());
        }
        var buchung = buchungRepository.insert(Buchung.uebernimmReservierung(reservierung));
        DomainEvents.publish(new ReservierungUebernommen(buchung.id(), reservierung.reservierungsnummer()));
        return buchung.id();
    }

    @Transactional
    @Publishes(domainEventTypes = BuchungStorniert.class)
    public void storniere(StorniereBuchung stornierung) {
        var buchung = buchungRepository.findById(stornierung.buchungsId())
            .orElseThrow(() -> new NichtGefundenException(
                "Die Buchung %s existiert nicht.".formatted(stornierung.buchungsId().value())));
        buchungRepository.update(buchung.storniere());
        DomainEvents.publish(new BuchungStorniert(buchung.id(), buchung.getHotelId()));
    }

    @Transactional
    public void checkeEin(CheckeEin checkIn) {
        checkInService.checkeEin(checkIn, LocalDate.now(clock));
    }

    /**
     * Checkt den Gast des Zimmers aus. Nur die Buchung aendert sich hier; das Zimmer wird erst als Reaktion auf
     * {@link GastAusgecheckt} freigegeben (eventual consistency, siehe {@link ZimmerApplicationService}).
     */
    @Transactional
    @Publishes(domainEventTypes = GastAusgecheckt.class)
    public void checkeAus(CheckeAus checkOut) {
        var zimmer = zimmerRepository.findByHotelUndZimmernummer(checkOut.hotelId(), checkOut.zimmernummer())
            .orElseThrow(() -> new NichtGefundenException(
                "Das Hotel hat kein Zimmer mit der Nummer %s.".formatted(checkOut.zimmernummer().value())));
        // Ist der Vorgaenger noch nicht ausgecheckt, reist zuerst der mit der fruehesten Anreise ab.
        var buchung = buchungRepository.findEingecheckteBuchungen(zimmer.id()).stream()
            .min(Comparator.comparing(b -> b.getAufenthaltszeitraum().anreise()))
            .orElseThrow(() -> new NichtGefundenException(
                "Im Zimmer %s ist kein Gast eingecheckt.".formatted(checkOut.zimmernummer().value())));

        var heute = LocalDate.now(clock);
        buchungRepository.update(buchung.checkeAus(heute));
        DomainEvents.publish(new GastAusgecheckt(buchung.id(), buchung.getHotelId(), zimmer.id(), heute));
    }

    @Transactional
    public Buchung.BuchungsId checkeSpontanEin(CheckeSpontanEin checkIn) {
        return checkInService.checkeSpontanEin(checkIn, LocalDate.now(clock)).id();
    }
}
