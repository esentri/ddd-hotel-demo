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

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.Gast;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.Identity;

import java.util.UUID;

/**
 * Diese Enum-Klasse dient zur Bereitstellung von Testdaten-IDs, die in den verschiedenen Teilen des Systems zur Demonstration verwendet werden.
 * Hierbei handelt es sich hauptsächlich um Identifikatoren für Hotel, Zimmer, Reservierung, Gast und ServiceLeistung.
 *
 * Jeder Enum-Wert besitzt eine Identität, welche in der jeweiligen Domaininstanz gespeichert wird.
 * So ermöglichen wir beispielsweise die Cache-Kohärenz bei komplexen Abfragen und garantieren die Datenintegrität auf Systemebene.
 *
 * Für Details zu den einzelnen Identitäten siehe die einzelnen Enum-Werte und die Methode {@link #id()}.
 *
 * @author Mario Herb
 */
public enum TestDataIds {

    HOTEL_ID(new Hotel.Id(UUID.randomUUID())),
    ZIMMER_ID_EINZELZIMMER(new Zimmer.ZimmerNummer("1")),
    ZIMMER_ID_BUSINESS_SUITE(new Zimmer.ZimmerNummer("2")),
    ZIMMER_ID_PRESIDENTIAL_SUITE(new Zimmer.ZimmerNummer("3")),
    RESERVIERUNG_ID_OFFEN(new Reservierung.ReservierungsNummer(UUID.randomUUID())),
    GAST_RESERVIERUNG_OFFEN_ID(new Gast.GastId(UUID.randomUUID())),
    RESERVIERUNG_ID_EINGECHECKT(new Reservierung.ReservierungsNummer(UUID.randomUUID())),
    GAST_RESERVIERUNG_EINGECHECKT_ID(new Gast.GastId(UUID.randomUUID())),
    SERVICE_LEISTUNG_ID(new ServiceLeistung.Id(UUID.randomUUID()))
    ;

    private Identity<?> id;

    TestDataIds(Identity<?> id) {
        this.id = id;
    }

    public <I extends Identity<?>> I id(){
        return (I) id;
    }

}
