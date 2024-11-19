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

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import io.domainlifecycles.domain.types.DomainCommand;

import java.util.List;

/**
 * Erstellt eine ServiceRechnung inklusive ReservierungsNummer,
 * einer Liste von Serviceleistungen und einer RechnungsAdresse.
 * Implementiert das Interface DomainCommand.
 *
 * @author Mario Herb
 *
 * @see Reservierung.ReservierungsNummer
 * @see ServiceLeistung.Id
 * @see Adresse
 * @see DomainCommand
 */
public record ErstelleServiceRechnung(
        Reservierung.ReservierungsNummer reservierungsNummer,
        List<ServiceLeistung.Id> serviceLeistungen,
        Adresse rechnungsAdresse
) implements DomainCommand {
}