package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;

import java.util.Objects;

/**
 * Domain Command, um den Checkout-Prozess fuer eine Buchung zu starten.
 */
public record CheckeGastAus(
    BuchungsId buchungsId
) implements DomainCommand {

    public CheckeGastAus {
        Objects.requireNonNull(buchungsId, "Die BuchungsId darf nicht null sein.");
    }
}
