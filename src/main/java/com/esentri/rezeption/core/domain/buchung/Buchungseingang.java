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

package com.esentri.rezeption.core.domain.buchung;

import com.esentri.rezeption.core.domain.zimmer.Belegung;
import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Dieser Service behandelt alle eingehenden Buchungen und delegiert die Verarbeitung an die entsprechenden Komponenten.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class Buchungseingang implements DomainService {

    private final Buchungen buchungen;

    private final ZimmerVerwaltung zimmerVerwaltung;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Diese Methode handhabt eine Anfrage zur Erstellung einer neuen Buchung.
     * Es prüft die Verfügbarkeit der Zimmer und wirft eine Ausnahme, wenn keine Zimmer verfügbar sind.
     *
     * @param erstelleNeueBuchung Ein DomoinCommand, welcher die Details der zu erstellenden Buchung enthält.
     * @return BuchungsNummer der erstellten Buchung oder wirft eine IllegalStateException, wenn keine Zimmer verfügbar sind.
     * @throws IllegalStateException wenn keine Zimmer zur Buchung verfügbar sind.
     */
    @Publishes(domainEventTypes = NeueBuchungErhalten.class)
    public Buchung.BuchungsNummer handle(ErstelleNeueBuchung erstelleNeueBuchung){
        var moeglicheZimmmer = zimmerVerwaltung.listZimmerByKategorieAndKapazitaet(
                erstelleNeueBuchung.hotelId(),
                erstelleNeueBuchung.gewuenschteZimmerKategorie(),
                erstelleNeueBuchung.gewuenschteKapazitaet()
        );
        var probeBelegung = new Belegung(
                erstelleNeueBuchung.geplanteAnkunftAm(),
                erstelleNeueBuchung.geplanteAnkunftAm().plusDays(erstelleNeueBuchung.geplanteAnzahlNaechte()),
                BelegungTyp.GAST
        );
        var anzahlUnbelegteZimmer = moeglicheZimmmer.stream().filter(z -> !z.hatUeberschneidendeBelegung(probeBelegung)).count();
        if(anzahlUnbelegteZimmer>0) {
            var buchung = buchungen.insert(neueBuchung(erstelleNeueBuchung));
            domainEventPublisher.publish(new NeueBuchungErhalten(buchung.id()));
            return buchung.id();
        }
        throw new IllegalStateException("Alle Zimmer bereits belegt!");
    }

    /**
     * Factory-Methode zum Erstellen einer neuen Buchung.
     * Der Gast wird ohne Detailinformationen erstellt und diese sollten später hinzugefügt werden.
     *
     * @param erstelleNeueBuchung Buchungsinformationen
     * @return  erstellte Buchung
     */
    private static Buchung neueBuchung(ErstelleNeueBuchung erstelleNeueBuchung){
        var buchung = new Buchung(
                new Buchung.BuchungsNummer(UUID.randomUUID()),
                erstelleNeueBuchung.hotelId(),
                erstelleNeueBuchung.geplanteAnkunftAm(),
                null,
                null,
                null,
                erstelleNeueBuchung.angebotenerZimmerpreis(),
                erstelleNeueBuchung.geplanteAnzahlNaechte(),
                erstelleNeueBuchung.gewuenschteZimmerKategorie(),
                erstelleNeueBuchung.gewuenschteKapazitaet(),
                new Gast(
                        new Gast.GastId(UUID.randomUUID()),
                        erstelleNeueBuchung.gastVorname(),
                        erstelleNeueBuchung.gastNachname(),
                        null,
                        null,
                        null,
                        null
                ),
                null
                ,0
        );
        return buchung;
    }
}
