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

import io.domainlifecycles.domain.types.DomainCommand;

/**
 * Ein DomainCommand zur Markierung einer Rechnung als bezahlt.
 * Dieses Command ändert den Zustand der Rechnung im System auf bezahlt.
 * Die Ausführung kann abgelehnt werden, wenn die Rechnung nicht existiert oder bereits bezahlt wurde.
 *
 *
 * @param rechnungId Die ID der Rechnung, die als bezahlt markiert werden soll.
 * @see com.esentri.rezeption.core.domain.rechnung.Rechnung
 * @author Mario Herb
 */
public record MarkiereRechnungBezahlt(Rechnung.Id rechnungId) implements DomainCommand {

}