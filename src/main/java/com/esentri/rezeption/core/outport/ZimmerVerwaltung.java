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

package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import io.domainlifecycles.domain.types.Repository;

import java.util.List;

/**
 * Das ZimmerVerwaltung Interface bietet Methoden zum Abrufen von Informationen über Zimmer in einem spezifizierten Hotel.
 * Die Methoden erlauben das Auflisten von Zimmern basierend auf dem Status, der Kategorie und der Kapazität.
 *
 * @author Mario Herb
 */
public interface ZimmerVerwaltung extends Repository<Zimmer.ZimmerNummer, Zimmer> {

    /**
     * Gibt eine Liste der Zimmer einer bestimmten Kategorie und Kapazität für ein spezifiziertes Hotel zurück.
     *
     * @param hotelId Die ID des Hotels, für das die Zimmer aufgelistet werden sollen
     * @param zimmerKategorie Die Kategorie der Zimmer, die aufgelistet werden sollen
     * @param kapazitaet Die Kapazität der Zimmer, die aufgelistet werden sollen
     * @return Eine Liste der Zimmer der bestimmten Kategorie und Kapazität
     */
    List<Zimmer> listZimmerByKategorieAndKapazitaet(Hotel.Id hotelId, ZimmerKategorie zimmerKategorie, int kapazitaet);

    /**
     * Gibt eine Liste der Zimmer für ein spezifiziertes Hotel zurück.
     *
     * @param hotelId Die ID des Hotels, für das die Zimmer aufgelistet werden sollen
     * @return Eine Liste der Zimmer für das angegebene Hotel
     */
    List<Zimmer> listByHotel(Hotel.Id hotelId);
}