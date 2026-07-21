package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Domain Event, das veroeffentlicht wird, wenn ein Gast erfolgreich ausgecheckt wurde.
 * Es enthaelt die minimal notwendigen Informationen fuer Folgeprozesse wie Housekeeping,
 * Feedback-E-Mails oder Marketing-Statistiken.
 */
public record GastAusgecheckt(
    @NotNull(message = "Die BuchungsId darf nicht null sein.") BuchungsId buchungsId,
    @NotNull(message = "Die ZimmerId darf nicht null sein.") ZimmerId zimmerId,
    @NotNull(message = "Der Zeitstempel fuer ausgechecktAm darf nicht null sein.") Instant ausgechecktAm
) implements DomainEvent {}
