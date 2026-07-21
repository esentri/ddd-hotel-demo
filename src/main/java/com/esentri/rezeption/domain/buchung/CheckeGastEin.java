package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

/**
 * Domain Command für den Check-in eines Gastes.
 * Bündelt die für den Prozess notwendigen Informationen.
 */
public record CheckeGastEin(
    @NotNull(message = "Die BuchungsId darf nicht null sein.") BuchungsId buchungsId,
    @NotNull(message = "Die ZimmerId darf nicht null sein.") ZimmerId zimmerId
) implements DomainCommand {}
