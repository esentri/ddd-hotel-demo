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

package com.esentri.rezeption.core.outport;
import nitrox.dlc.domain.types.DomainEvent;

/**
 * Dies ist die Schnittstelle für den DomainEventPublisher.
 * Der DomainEventPublisher ist verantwortlich für das Verteilen (Publishing) von DomainEvents.
 *
 * DomainEvents werden verwendet, um zu verdeutlichen, dass etwas passiert ist, das für Domänenexperten von Bedeutung ist.
 * DomainEvents werden häufig verwendet, um Domänenregeln in Form einer Choreographie auszudrücken:
 * "Wenn das Ereignis A passiert ist, muss B typischerweise etwas tun". Auf diese Weise ermöglichen DomainEvents eine gute Trennung
 * von Aufgaben für verschiedene Aggregates, die mit weniger Kopplung untereinander spielen.
 * Mit Techniken wie Event Sourcing oder CQRS können DomainEvents kombiniert werden, um mit einer expliziten Historie arbeiten zu können
 * dessen, was in der Domäne passiert ist (Audit Trail), oder um die Leistung und Skalierbarkeit des Systems zu verbessern.
 * Neben rein technischen Vorteilen macht der Einsatz von DomainEvents wichtige Nebeneffekte innerhalb der Domäne explizit und verbessert damit die
 * Lesbarkeit und Verständlichkeit des Codes.
 *
 * @author Mario Herb
 */
public interface DomainEventPublisher {

    /**
     * Veröffentlicht ein gegebenes DomainEvent.
     *
     * @param domainEvent Das zu veröffentlichende DomainEvent.
     */
    void publish(DomainEvent domainEvent);
}
