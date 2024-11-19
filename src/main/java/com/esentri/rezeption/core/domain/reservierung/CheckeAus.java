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

import io.domainlifecycles.domain.types.DomainCommand;

/**
 * Die Klasse CheckeAus repräsentiert einen Befehl zum Auschecken einer Reservierung.
 * Es implementiert das Interface DomainCommand und sorgt für die korrekte Ausführung des Ablaufs.
 *
 * Es wird verwendet, um eine Anfrage zum Auschecken einer Reservierung zu repräsentieren
 * und enthält die Reservierungsnummer, die ausgecheckt werden muss.
 *
 * @author Mario Herb
 * @see DomainCommand
 */
public record CheckeAus(Reservierung.ReservierungsNummer reservierungsNummer) implements DomainCommand {
}