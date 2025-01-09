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

package com.esentri.rezeption.core.domain.serviceleistung;

import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.buchung.Buchung;
import io.domainlifecycles.domain.types.DomainCommand;

/**
 * Diese Klasse wird beim Erstellen einer Serviceleistung verwendet.
 * Sie enthält die BuchungsNummer, den Servicetyp und den Nettopreis einer Serviceleistung.
 * Sie implementiert das DomainCommand-Interface, das dafür sorgt,
 * dass Aktionen explizit beschrieben und auf Aggregaten durchgeführt werden können.
 *
 * @author Mario Herb
 * @see DomainCommand
 * @see Buchung
 * @see ServiceTyp
 * @see Preis
 */
public record ErstelleServiceLeistung(Buchung.BuchungsNummer buchungsNummer, ServiceTyp serviceTyp, Preis nettoPreis) implements DomainCommand {
}