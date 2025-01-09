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
import io.domainlifecycles.domain.types.DomainEvent;

import java.time.LocalDate;


/**
 * Ein DomainEvent, das darstellt, dass eine Buchung eingecheckt wurde.
 * Bei dem Event werden die BuchungsNummer, die ZimmerNummer und der Buchungszeitraum festgehalten.
 *
 * @param buchungsNummer Die Identifikationsnummer der Buchung
 * @param zimmerNummer Die Identifikationsnummer des Zimmers
 * @param von Das Startdatum der Buchung
 * @param bis Das Enddatum der Buchung
 *
 * @author Mario Herb
 */
public record BuchungEingecheckt(Buchung.BuchungsNummer buchungsNummer,
                                 Zimmer.ZimmerNummer zimmerNummer,
                                 LocalDate von,
                                 LocalDate bis
) implements DomainEvent {}