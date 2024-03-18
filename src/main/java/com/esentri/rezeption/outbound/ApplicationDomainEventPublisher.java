package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.outport.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.DomainEvent;
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
