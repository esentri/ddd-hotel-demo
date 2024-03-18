package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import com.esentri.rezeption.core.outport.ZimmerRepository;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.DomainService;
import nitrox.dlc.domain.types.Publishes;

import java.util.UUID;

/**
 * Dieser Service behandelt alle eingehenden Reservierungen und delegiert die Verarbeitung an die entsprechenden Komponenten.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class ReservierungsEingangService implements DomainService {

    private final ReservierungRepository reservierungRepository;

    private final ZimmerRepository zimmerRepository;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Diese Methode handhabt eine Anfrage zur Erstellung einer neuen Reservierung.
     * Es prüft die Verfügbarkeit der Zimmer und wirft eine Ausnahme, wenn keine Zimmer verfügbar sind.
     *
     * @param erstelleNeueReservierung Ein DomoinCommand, welcher die Details der zu erstellenden Reservierung enthält.
     * @return ReservierungsNummer der erstellten Reservierung oder wirft eine IllegalStateException, wenn keine Zimmer verfügbar sind.
     * @throws IllegalStateException wenn keine Zimmer zur Reservierung verfügbar sind.
     */
    @Publishes(domainEventTypes = NeueReservierungErhalten.class)
    public Reservierung.ReservierungsNummer handle(ErstelleNeueReservierung erstelleNeueReservierung){
        var moeglicheZimmmer = zimmerRepository.listZimmerByKategorieAndKapazitaet(
                erstelleNeueReservierung.hotelId(),
                erstelleNeueReservierung.gewuenschteZimmerKategorie(),
                erstelleNeueReservierung.gewuenschteKapazitaet()
        );
        var probeBelegung = new Belegung(
                erstelleNeueReservierung.geplanteAnkunftAm(),
                erstelleNeueReservierung.geplanteAnkunftAm().plusDays(erstelleNeueReservierung.geplanteAnzahlNaechte()),
                BelegungTyp.GAST
        );
        var anzahlUnbelegteZimmer = moeglicheZimmmer.stream().filter(z -> !z.hatUeberschneidendeBelegung(probeBelegung)).count();
        if(anzahlUnbelegteZimmer>0) {
            var reservierung = reservierungRepository.insert(neueReservierung(erstelleNeueReservierung));
            domainEventPublisher.publish(new NeueReservierungErhalten(reservierung.id()));
            return reservierung.id();
        }
        throw new IllegalStateException("Alle Zimmer bereits belegt!");
    }

    /**
     * Methode zum Erstellen einer neuen Reservierung.
     * Der Gast wird ohne Detailinformationen erstellt und diese sollten später hinzugefügt werden.
     *
     * @param erstelleNeueReservierung Reservierungsinformationen
     * @return  erstellte Reservierung
     */
    private static Reservierung neueReservierung(ErstelleNeueReservierung erstelleNeueReservierung){
        var reservierung = new Reservierung(
                new Reservierung.ReservierungsNummer(UUID.randomUUID()),
                erstelleNeueReservierung.hotelId(),
                erstelleNeueReservierung.geplanteAnkunftAm(),
                null,
                null,
                null,
                erstelleNeueReservierung.angebotenerZimmerpreis(),
                erstelleNeueReservierung.geplanteAnzahlNaechte(),
                erstelleNeueReservierung.gewuenschteZimmerKategorie(),
                erstelleNeueReservierung.gewuenschteKapazitaet(),
                new Gast(
                        new Gast.GastId(UUID.randomUUID()),
                        erstelleNeueReservierung.gastVorname(),
                        erstelleNeueReservierung.gastNachname(),
                        null,
                        null,
                        null,
                        null
                ),
                null
                ,0
        );
        return reservierung;
    }
}
