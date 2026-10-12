package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record Aufenthaltszeitraum(@NotNull LocalDate anreise, @NotNull LocalDate abreise) implements ValueObject {

    @Override
    public void validate() {
        DomainAssertions.isTrue(anreise.isBefore(abreise), "Die Anreise muss vor der Abreise liegen.");
    }
}
