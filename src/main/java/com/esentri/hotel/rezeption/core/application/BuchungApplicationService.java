package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungStorniert;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckInService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckOutService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeSpontanEin;
import com.esentri.hotel.rezeption.core.domain.buchung.ReservierungUebernommen;
import com.esentri.hotel.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.hotel.rezeption.core.domain.buchung.UebernimmReservierung;
import io.domainlifecycles.domain.types.ApplicationService;
import io.domainlifecycles.domain.types.Publishes;
import io.domainlifecycles.events.api.DomainEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

public class BuchungApplicationService implements ApplicationService {

    private final BuchungRepository buchungRepository;
    private final CheckInService checkInService;
    private final CheckOutService checkOutService;
    private final Clock clock;

    public BuchungApplicationService(BuchungRepository buchungRepository, CheckInService checkInService,
                                     CheckOutService checkOutService, Clock clock) {
        this.buchungRepository = buchungRepository;
        this.checkInService = checkInService;
        this.checkOutService = checkOutService;
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

    @Transactional
    public void checkeAus(CheckeAus checkOut) {
        checkOutService.checkeAus(checkOut, LocalDate.now(clock));
    }

    @Transactional
    public Buchung.BuchungsId checkeSpontanEin(CheckeSpontanEin checkIn) {
        return checkInService.checkeSpontanEin(checkIn, LocalDate.now(clock)).id();
    }
}
