package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.RechnungRepository;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import com.esentri.rezeption.core.outport.ServiceLeistungRepository;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.DomainService;
import nitrox.dlc.domain.types.Publishes;

/**
 * DomainService zur Unterstützung der Abfertigung von Auscheck-Ooperationen.
 * Der Service nutzt eine Zusammenarbeit von Repositories und Domain-Klassen, um ein vollumfängliches Auschecken zu ermöglichen.
 * Enthält alle notwendigen Repositories und Services zum Validieren und Durchführen einer Check-out-Operation.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class CheckOutService implements DomainService {

    private final ReservierungRepository reservierungRepository;

    private final RechnungRepository rechnungRepository;

    private final ServiceLeistungRepository serviceLeistungRepository;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Behandelt den Auscheckvorgang für eine gegebene Reservierungsnummer.
     *
     * Wirft eine IllegalStateException, wenn die Zimmerabrechnung noch nicht erfolgt ist oder Serviceleistungen noch nicht abgerechnet sind.
     * Veröffentlicht ein ReservierungAusgecheckt Ereignis.
     *
     * @param checkeAus Das Auscheck-Objekt, das Informationen über die Reservierungsnummer enthält.
     * @return Die Reservierungsnummer der Auscheck-Operation.
     * @throws IllegalStateException wenn die Zimmerabrechnung noch nicht erfolgt ist oder wenn Serviceleistungen noch nicht abgerechnet sind.
     */
    @Publishes(domainEventTypes = ReservierungAusgecheckt.class)
    public Reservierung.ReservierungsNummer handle(CheckeAus checkeAus){

        var rechnungen = rechnungRepository.findByReservierungsNummer(checkeAus.reservierungsNummer());

        var zimmerRechnungExistiert = rechnungen.stream().anyMatch(Rechnung::beinhaltetZimmerAbrechnung);

        if(!zimmerRechnungExistiert){
            throw new IllegalStateException("Vor dem Auschecken muss die Zimmerabrechnung erfolgen!");
        }

        var offeneServiceLeistungenExistieren = serviceLeistungRepository.find(checkeAus.reservierungsNummer())
                .stream().anyMatch(sl -> sl.getAbgerechnetPer() == null);

        if(offeneServiceLeistungenExistieren){
            throw new IllegalStateException("Es wurden nicht alle ServiceLeistungen der Reservierung abgerechnet!");
        }

        var reservierung = reservierungRepository.findById(checkeAus.reservierungsNummer())
            .map( r ->
                    reservierungRepository.update(r.checkeAus()))
            .orElseThrow();

        domainEventPublisher.publish(new ReservierungAusgecheckt(reservierung.getReservierungsNummer(), reservierung.getCheckOutAm(), reservierung.getZimmerNummer()));

        return  reservierung.id();
    }

}
