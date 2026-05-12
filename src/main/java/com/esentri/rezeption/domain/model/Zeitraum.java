package com.esentri.rezeption.domain.model;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Zeitraum(
    @NotNull LocalDate von,
    @NotNull LocalDate bis
) implements ValueObject {

    public Zeitraum {
        DomainAssertions.isTrue(von.isBefore(bis) || von.isEqual(bis), "Das Startdatum muss vor oder am Enddatum liegen.");
    }

    public boolean überschneidet(Zeitraum anderer) {
        return !von.isAfter(anderer.bis) && !bis.isBefore(anderer.von);
    }
}
