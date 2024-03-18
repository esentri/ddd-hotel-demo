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
