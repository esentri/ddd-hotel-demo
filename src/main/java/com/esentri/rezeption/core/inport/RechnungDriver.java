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

package com.esentri.rezeption.core.inport;

import com.esentri.rezeption.core.domain.rechnung.ErstelleRechnungFuerReservierung;
import com.esentri.rezeption.core.domain.rechnung.ErstelleServiceRechnung;
import com.esentri.rezeption.core.domain.rechnung.MarkiereRechnungBezahlt;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import nitrox.dlc.domain.types.Driver;

/**
 * Das Interface RechnungDriver stellt eine Schnittstelle für die Bearbeitung von Rechnungen zur Verfügung.
 * Es definiert Methoden zur Erstellung von Rechnungsaufträgen und zur Markierung von bereits bezahlten Rechnungen.
 *
 * @author Mario Herb
 * @see Rechnung
 * @see ErstelleRechnungFuerReservierung
 * @see ErstelleServiceRechnung
 * @see MarkiereRechnungBezahlt
 */
public interface RechnungDriver extends Driver {

    /**
     * Nimmt einen Command zur Markierung einer Rechnung als bezahlt entgegen und gibt die ID der bearbeiteten Rechnung zurück.
     *
     * @param markiereRechnungBezahlt ein Command, der die zu zahlende Rechnung angibt
     * @return die ID der bezahlten Rechnung
     */
    Rechnung.RechnungId handle(MarkiereRechnungBezahlt markiereRechnungBezahlt);

    /**
     * Nimmt einen Command zur Erstellung einer Rechnung für eine Reservierung entgegen und gibt die ID der erstellten Rechnung zurück.
     *
     * @param erstelleRechnungFuerReservierung ein Command, der die Details für die zu erstellende Rechnung angibt
     * @return die ID der erstellten Rechnung
     */
    Rechnung.RechnungId handle(ErstelleRechnungFuerReservierung erstelleRechnungFuerReservierung);

    /**
     * Nimmt einen Command zur Erstellung einer Service-Rechnung entgegen und gibt die ID der erstellten Rechnung zurück.
     *
     * @param erstelleServiceRechnung ein Command, der die Details für die zu erstellende Rechnung angibt
     * @return die ID der erstellten Rechnung
     */
    Rechnung.RechnungId handle(ErstelleServiceRechnung erstelleServiceRechnung);
}