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
import io.domainlifecycles.domain.types.DomainEvent;

/**
 * Repräsentiert das Domain-Event, das ausgelöst wird, wenn eine neue Buchung erhalten wird.
 * Dieses record implementiert das DomainEvent-Interface und enthält eine einzige Eigenschaft,
 * die die BuchungsNummer enthält.
 *
 * @author Mario Herb
 * @see DomainEvent
 */
public record NeueBuchungErhalten(Buchung.BuchungsNummer buchungsNummer) implements DomainEvent {
}