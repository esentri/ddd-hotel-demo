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

package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.outport.DomainEventPublisher;
import io.domainlifecycles.domain.types.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Dies ist die Implementierung des DomainEventPublisher-Interfaces in der Anwendungsschicht.
 * Der DomainEventPublisher ist verantwortlich für das Verteilen (Publishing) von DomainEvents.
 * DomainEvents werden verwendet, um zu verdeutlichen, dass etwas passiert ist, das für Domänenexperten von Bedeutung ist.
 *
 * In diesem Sinne veröffentlicht diese Implementierung der DomainEventPublisher-Schnittstelle DomainEvents mithilfe
 * des von Spring bereitgestellten ApplicationEventPublisher.
 *
 * @author Mario Herb
 * @see DomainEventPublisher
 */
@Service
@RequiredArgsConstructor
public class ApplicationDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Veröffentlicht ein gegebenes DomainEvent durch Delegierung an den ApplicationEventPublisher von Spring.
     *
     * @param domainEvent Das zu veröffentlichende DomainEvent.
     * @see DomainEvent
     */
    @Override
    public void publish(DomainEvent domainEvent) {
        applicationEventPublisher.publishEvent(domainEvent);
    }
}
