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

import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.DomainCommand;

import java.util.Objects;

/**
 * Record-Klasse für den Check-In-Vorgang in einer Buchung.
 * Enthält die BuchungsNummer, die ZimmerNummer und die geplante Anzahl an Nächten.
 * Implementiert das DomainCommand Interface.
 *
 * @author Mario Herb
 */
public record CheckeEin(
        Buchung.BuchungsNummer buchungsNummer,
        Zimmer.ZimmerNummer zimmerNummer,
        int geplanteAnzahlNaechte
        ) implements DomainCommand {

        public CheckeEin(
                Buchung.BuchungsNummer buchungsNummer,
                Zimmer.ZimmerNummer zimmerNummer,
                int geplanteAnzahlNaechte
        ) {
                this.buchungsNummer = Objects.requireNonNull(
                        buchungsNummer,
                        "Die Angabe einer BuchungsNummer ist für den CheckIn erforderlich!"
                );
                this.zimmerNummer = Objects.requireNonNull(
                        zimmerNummer,
                        "Die Angabe einer ZimmerNummer ist für den CheckIn erforderlich!"
                );
                if(geplanteAnzahlNaechte < 1) {
                        throw new IllegalArgumentException(" Die geplante Anzahl Naechte muss größer 0 sein!");
                }
                this.geplanteAnzahlNaechte = geplanteAnzahlNaechte;
        }
}
