package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.ZimmerId;
import io.domainlifecycles.domain.types.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event, das veroeffentlicht wird, wenn ein Gast erfolgreich ausgecheckt wurde.
 * Es enthaelt die minimal notwendigen Informationen fuer Folgeprozesse wie Housekeeping,
 * Feedback-E-Mails oder Marketing-Statistiken.
 */
public record GastAusgecheckt(
    BuchungsId buchungsId,
    Instant ausgechecktAm
) implements DomainEvent {

    /**
     * Compact Constructor zur Validierung der Invarianten.
     * Stellt sicher, dass alle Felder gesetzt sind.
     */
    public GastAusgecheckt {
        Objects.requireNonNull(buchungsId, "Die BuchungsId darf nicht null sein.");
        Objects.requireNonNull(ausgechecktAm, "Der Zeitstempel fuer ausgechecktAm darf nicht null sein.");
    }
}
