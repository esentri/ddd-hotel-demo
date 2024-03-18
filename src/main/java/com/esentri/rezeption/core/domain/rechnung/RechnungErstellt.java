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

package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import nitrox.dlc.domain.types.DomainEvent;

import java.util.List;

/**
 * Die Klasse RechnungErstellt dient dazu, Ereignisse zu repräsentieren, bei denen eine Rechnung erstellt wurde.
 * Sie ist ein record und implementiert das Interface DomainEvent.
 *
 * Eine instanziierte RechnungErstellt enthält eine Rechnungs-ID, eine Reservierungsnummer,
 * einen Gesamtnettopreis und eine Liste von abgerechneten Dienstleistungen.
 *
 * @author Mario Herb
 */
public record RechnungErstellt(Rechnung.RechnungId rechnungId,
                               Reservierung.ReservierungsNummer reservierungsNummer,
                               Preis gesamtNetto,
                               List<ServiceLeistung.LeistungsId> abgerechneteServices) implements DomainEvent {
}
