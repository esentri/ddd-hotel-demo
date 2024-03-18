package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.RechnungRepository;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import com.esentri.rezeption.core.outport.ServiceLeistungRepository;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.DomainService;
import nitrox.dlc.domain.types.Publishes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stellt einen Service dar, der zum Erstellen von Rechnungen für Reservierungen und
 * Serviceleistungen verwendet wird. Abhängigkeiten auf Repositories und EventPublisher
 * sind notwendig.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class RechnungsErstellungsService implements DomainService {

    private final RechnungRepository rechnungRepository;

    private final ReservierungRepository reservierungRepository;

    private final ServiceLeistungRepository serviceLeistungRepository;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Verarbeite die Anfrage zur Erstellung einer Rechnung für eine Reservierung.
     * Die Anforderung enthält die Reservierungsnummer und eventuelle Serviceleistungen.
     * Nach dem Erstellen der Rechnung wird ein RechnungErstellt-Ereignis veröffentlicht.
     *
     * @param erstelleRechnungFuerReservierung die Anforderung zur Erstellung einer Rechnung für eine Reservierung.
     * @return die ID der erstellten Rechnung.
     */
    @Publishes(domainEventTypes = RechnungErstellt.class)
    public Rechnung.RechnungId handle(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung){
        var reservierung = reservierungRepository.findById(erstelleRechnungFuerReservierung.reservierungsNummer()).orElseThrow();
        var serviceLeistungen = serviceLeistungRepository.find(erstelleRechnungFuerReservierung.reservierungsNummer())
                .stream().filter(sl -> erstelleRechnungFuerReservierung.serviceLeistungen().contains(sl.id()))
                .toList();

        var neueRechnung = neueRechnungFuerReservierung(
                erstelleRechnungFuerReservierung,
                reservierung.getAngebotenerZimmerpreis(),
                erstelleRechnungFuerReservierung.rechnungsAdresse(),
                serviceLeistungen
        );

        rechnungRepository.insert(neueRechnung);
        domainEventPublisher.publish(new RechnungErstellt(
                    neueRechnung.id(),
                    neueRechnung.getReservierungsNummer(),
                    neueRechnung.getGesamtNetto(),
                    serviceLeistungen.stream().map(ServiceLeistung::id).toList()
                )
        );
        return neueRechnung.id();
    }

    /**
     * Verarbeitet die Anforderung zum Erstellen einer Rechnung für Serviceleistungen.
     * Die Anforderung enthält die Reservierungsnummer und Serviceleistungen.
     * Nach der Erstellung der Rechnung wird ein RechnungErstellt-Ereignis veröffentlicht.
     *
     * @param erstelleServiceRechnung die Anforderung zum Erstellen einer Rechnung für eine Serviceleistung.
     * @return die ID der erstellten Rechnung.
     */
    @Publishes(domainEventTypes = RechnungErstellt.class)
    public Rechnung.RechnungId handle(ErstelleServiceRechnung erstelleServiceRechnung){
        var serviceLeistungen = serviceLeistungRepository.find(erstelleServiceRechnung.reservierungsNummer())
                .stream().filter(sl -> erstelleServiceRechnung.serviceLeistungen().contains(sl.id()))
                .toList();
        var neueRechnung = neueServiceRechnung(
                erstelleServiceRechnung,
                erstelleServiceRechnung.rechnungsAdresse(),
                serviceLeistungen
        );

        rechnungRepository.insert(neueRechnung);
        domainEventPublisher.publish(new RechnungErstellt(
                neueRechnung.id(),
                neueRechnung.getReservierungsNummer(),
                neueRechnung.getGesamtNetto(),
                serviceLeistungen.stream().map(ServiceLeistung::id).toList()
                ));
        return neueRechnung.id();
    }

    private static Rechnung neueRechnungFuerReservierung(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung,
                                                        Preis zimmerPreis,
                                                        Adresse rechnungsAdresse,
                                                        List<ServiceLeistung> serviceLeistungen){
        var rechnung = new Rechnung(
                new Rechnung.RechnungId(UUID.randomUUID()),
                erstelleRechnungFuerReservierung.reservierungsNummer(),
                zimmerPreis,
                LocalDateTime.now(),
                rechnungsAdresse
        );
        serviceLeistungen.forEach(sl -> rechnung.addRechnungsPosition(sl.getNettoPreis(), sl.getBeschreibung(), sl.id()));
        return rechnung;
    }

    private static Rechnung neueServiceRechnung(ErstelleServiceRechnung erstelleServiceRechnung,
                                                Adresse rechnungsAdresse,
                                                List<ServiceLeistung> serviceLeistungen
    ){
        var rechnung = new Rechnung(
                new Rechnung.RechnungId(UUID.randomUUID()),
                erstelleServiceRechnung.reservierungsNummer(),
                null,
                LocalDateTime.now(),
                rechnungsAdresse
        );
        serviceLeistungen.forEach(sl -> rechnung.addRechnungsPosition(sl.getNettoPreis(), sl.getBeschreibung(), sl.id()));
        return rechnung;
    }
}
