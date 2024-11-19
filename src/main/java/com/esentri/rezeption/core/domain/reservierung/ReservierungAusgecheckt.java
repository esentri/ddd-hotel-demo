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

package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.DomainEvent;

import java.time.LocalDateTime;

/**
 * Diese Klasse modelliert das Auscheck-Event einer Hotelreservierung.
 * Das Event wird ausgelöst, wenn ein Gast aus seinem reservierten Zimmer auscheckt.
 * Die Klasse enthält die Reservierungsnummer der betroffenen Reservierung, das Datum und die Uhrzeit des Auscheckens und die Zimmernummer des betroffenen Zimmers.
 *
 * @author Mario Herb
 */
public record ReservierungAusgecheckt(Reservierung.ReservierungsNummer reservierungsNummer,
                                      LocalDateTime am,
                                      Zimmer.ZimmerNummer zimmerNummer) implements DomainEvent {
}
