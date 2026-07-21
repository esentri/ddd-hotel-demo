package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

/**
 * Domain Command, um den Checkout-Prozess fuer eine Buchung zu starten.
 */
public record CheckeGastAus(
    @NotNull(message = "Die BuchungsId darf nicht null sein.") BuchungsId buchungsId
) implements DomainCommand {}
