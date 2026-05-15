package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;

import java.time.LocalDate;

public record Zeitraum(LocalDate checkInDatum, LocalDate checkOutDatum) implements ValueObject {
    public Zeitraum {
        if (checkInDatum == null || checkOutDatum == null) {
            throw new IllegalArgumentException("Daten duerfen nicht null sein");
        }
        if (!checkOutDatum.isAfter(checkInDatum)) {
            throw new IllegalArgumentException("Check-out Datum muss nach dem Check-in Datum liegen");
        }
    }
}
