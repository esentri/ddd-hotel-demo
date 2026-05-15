package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record Zeitraum(LocalDate checkInDatum, LocalDate checkOutDatum) implements ValueObject {
    public Zeitraum {
        if (checkInDatum == null || checkOutDatum == null) {
            throw new IllegalArgumentException("Check-in und Check-out Datum duerfen nicht null sein.");
        }
        if (!checkOutDatum.isAfter(checkInDatum)) {
            throw new IllegalArgumentException("Das Check-out Datum muss nach dem Check-in Datum liegen.");
        }
    }
}
