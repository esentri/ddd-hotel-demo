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

import com.esentri.rezeption.core.domain.rechnung.RechnungErstellt;
import com.esentri.rezeption.core.domain.serviceleistung.ErstelleServiceLeistung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.Driver;

/**
 * Interface für den ServiceLeistungDriver. Dieses Interface bietet Methoden zur Handhabung von Serviceleistungen.
 * Es enthält Methoden, um ServiceLeistungen zu erstellen und auf RechnungErstellt Ereignisse zu reagieren.
 *
 * @author Mario Herb
 *
 */
public interface ServiceLeistungDriver extends Driver {

    /**
     * Erstellt eine Serviceleistung und gibt eine LeistungsId zurück.
     *
     * @param erstelleServiceLeistung Command, das Informationen zum Erstellen einer Serviceleistung enthält.
     * @return LeistungsId der erstellten Serviceleistung.
     */
    ServiceLeistung.LeistungsId handle(ErstelleServiceLeistung erstelleServiceLeistung);

    /**
     * Diese Methode wird aufgerufen, wenn ein RechnungErstellt Ereignis eintritt.
     *
     * @param rechnungErstellt Das RechnungErstellt Ereignis, auf das die Methode reagiert.
     */
    void onEvent(RechnungErstellt rechnungErstellt);
}