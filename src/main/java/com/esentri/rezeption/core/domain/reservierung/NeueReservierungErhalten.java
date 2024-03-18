package com.esentri.rezeption.core.domain.reservierung;
import nitrox.dlc.domain.types.DomainEvent;

/**
 * Repräsentiert das Domain-Event, das ausgelöst wird, wenn eine neue Reservierung erhalten wird.
 * Dieses record implementiert das DomainEvent-Interface und enthält eine einzige Eigenschaft,
 * die die Reservierungsnummer enthält.
 *
 * @author Mario Herb
 * @see DomainEvent
 */
public record NeueReservierungErhalten(Reservierung.ReservierungsNummer reservierungsNummer) implements DomainEvent {
}