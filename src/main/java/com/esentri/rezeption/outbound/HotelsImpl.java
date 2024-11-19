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
import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.outport.Hotels;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Diese Klasse ist eine Implementierung des Hotels-Interfaces und erweitert das BaseInMemoryRepository mit Hotel als Entitätstyp und Id als Identitätstyp.
 * Sie nutzt die In-Memory-Speicherstrategie zur Verwaltung von Hotel-Objekten.
 * Beim Erzeugen der Instanz wird bereits ein Hotel-Objekt zur Liste aller Aggregate hinzugefügt.
 *
 *
 *
 * @author Mario Herb
 * @see BaseInMemoryRepository
 * @see Hotels
 * @see Repository
 */
@Repository
public class HotelsImpl extends BaseInMemoryRepository<Hotel.Id, Hotel> implements Hotels {

    /**
     * Der Konstruktor erstellt eine Instanz von HotelsImpl und initialisiert das zugehörige In-Memory-Repository mit einer leeren Liste.
     * Anschließend fügt er ein Hotel-Objekt mit Testdaten zur Liste der Aggregate hinzu.
     * Solche Initialisierung wird in der Regel für Testzwecke verwendet.
     */
    public HotelsImpl() {
        super(new ArrayList<>());
        allAggregates.add(
                new Hotel(
                        TestDataIds.HOTEL_ID.id(),
                        "MeinSuperHotel",
                        Adresse.builder()
                                .ort("Karlsruhe")
                                .strasse("Prachtstrasse")
                                .hausnummer("1a")
                                .postleitzahl("76133")
                                .build()
                )
        );
    }
}
