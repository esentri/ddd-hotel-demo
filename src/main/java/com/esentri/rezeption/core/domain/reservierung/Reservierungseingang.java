/*
 *  Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Reservierungen;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import lombok.RequiredArgsConstructor;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;

import java.util.UUID;

/**
 * Dieser Service behandelt alle eingehenden Reservierungen und delegiert die Verarbeitung an die entsprechenden Komponenten.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class Reservierungseingang implements DomainService {

    private final Reservierungen reservierungen;

    private final ZimmerVerwaltung zimmerVerwaltung;

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
        var moeglicheZimmmer = zimmerVerwaltung.listZimmerByKategorieAndKapazitaet(
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
            var reservierung = reservierungen.insert(neueReservierung(erstelleNeueReservierung));
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
