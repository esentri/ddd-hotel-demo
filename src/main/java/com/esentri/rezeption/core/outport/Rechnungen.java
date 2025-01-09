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

import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import io.domainlifecycles.domain.types.Repository;

import java.util.List;

/**
 * Das Rechnungen beschreibt Methoden für den Zugriff auf Rechnungen in der zugrundeliegenden Datenquelle.
 *
 * Es definiert die Möglichkeit, Rechnungen basierend auf der BuchungsNummer zu finden.
 *
 * @author Mario Herb
 */
public interface Rechnungen extends Repository<Rechnung.Id, Rechnung> {

    /**
     * Sucht alle Rechnungen, die der spezifizierten BuchungsNummer zugeordnet sind.
     *
     * @param buchungsNummer Die BuchungsNummer, nach der gesucht werden soll.
     * @return Eine Liste von Rechnungen, die der bereitgestellten BuchungsNummer entsprechen.
     */
    List<Rechnung> findByBuchungsNummer(Buchung.BuchungsNummer buchungsNummer);

}
