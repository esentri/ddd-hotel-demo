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

import com.esentri.rezeption.core.domain.Adresse;
import io.domainlifecycles.domain.types.DomainCommand;

import java.time.LocalDate;

/**
 * Dieser Record repräsentiert die Aktion, die Gastdaten einer bestimmten Buchung zu vervollständigen.
 * Die Aktion kann durchgeführt werden, indem alle erforderlichen Daten des Gastes bereitgestellt werden,
 * wie Vorname, Nachname, Geburtsdatum, Email-Adresse, Telefonnummer und Heimadresse.
 * Dieses Command wird zur Modifikation des zugehörigen Aggregates, Buchung, verwendet.
 *
 * @author Mario Herb
 */
public record VervollstaendigeGastDaten(
        Buchung.BuchungsNummer buchungsNummer,
        String vorname,
        String nachname,
        LocalDate geburtsdatum,
        EmailAdresse emailAdresse,
        TelefonNummer telefonNummer,
        Adresse heimAdresse
) implements DomainCommand {}