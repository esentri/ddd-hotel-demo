package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.ZimmerId;
import io.domainlifecycles.domain.types.DomainCommand;

import java.util.Objects;

/**
 * Domain Command für den Check-in eines Gastes.
 * Bündelt die für den Prozess notwendigen Informationen.
 */
public record CheckeGastEin(
    BuchungsId buchungsId,
    ZimmerId zimmerId
) implements DomainCommand {

    public CheckeGastEin {
        Objects.requireNonNull(buchungsId, "Die BuchungsId darf nicht null sein.");
        Objects.requireNonNull(zimmerId, "Die ZimmerId darf nicht null sein.");
    }
}
