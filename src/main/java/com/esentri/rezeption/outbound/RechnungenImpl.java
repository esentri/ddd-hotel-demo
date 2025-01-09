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

package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.outport.Rechnungen;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine konkrete Implementierung des Rechnungen.
 * Diese Klasse erbt die Funktionalität von BaseInMemoryRepository und implementiert die spezifische Logik
 * zum Abrufen von Rechnungen nach BuchungsNummer.
 * Es wird mit der @Repository Annotation gekennzeichnet, um die automatische Erkennung
 * durch den Spring Framework Component Scan zu ermöglichen.
 *
 * @author Mario Herb
 * @see BaseInMemoryRepository
 * @see Rechnungen
 * @see Rechnung
 * @see Buchung
 */

@Repository
public class RechnungenImpl extends BaseInMemoryRepository<Rechnung.Id, Rechnung> implements Rechnungen {

    public RechnungenImpl() {
        super(new ArrayList<>());
    }

    /**
     * Findet und gibt Rechnungsobjekte zurück, die mit der gegebenen BuchungsNummer verknüpft sind.
     * Es verwendet die Java-Stream-API, um den Rechnungen-Stream zu filtern und
     * alle Rechnungen zu sammeln, die die Bedingung erfüllen.
     *
     * @param buchungsNummer die BuchungsNummer, nach der gesucht wird.
     * @return Eine Liste von Rechnungsobjekten, die mit der angegebenen BuchungsNummer verknüpft sind.
     */
    @Override
    public List<Rechnung> findByBuchungsNummer(Buchung.BuchungsNummer buchungsNummer) {
        return allAggregates.stream().filter(r -> buchungsNummer.equals(r.getBuchungsNummer())).toList();
    }
}