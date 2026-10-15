package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Zeitraum einer Buchung von der Anreise bis zur Abreise; die Abreise liegt nach der Anreise.
 */
@Builder
public record Buchungszeitraum(@NotNull LocalDate anreise, @NotNull LocalDate abreise) implements ValueObject {

    @Override
    public void validate() {
        if (anreise != null && abreise != null) {
            DomainAssertions.isTrue(abreise.isAfter(anreise), "Die Abreise muss nach der Anreise liegen.");
        }
    }
}
