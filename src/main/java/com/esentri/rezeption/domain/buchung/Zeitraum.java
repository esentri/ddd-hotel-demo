package com.esentri.rezeption.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

public record Zeitraum(LocalDate checkInDatum, LocalDate checkOutDatum) implements ValueObject {
    public Zeitraum {
        Objects.requireNonNull(checkInDatum, "Check-in Datum darf nicht null sein");
        Objects.requireNonNull(checkOutDatum, "Check-out Datum darf nicht null sein");
        if (!checkOutDatum.isAfter(checkInDatum)) {
            throw new IllegalArgumentException("Check-out Datum muss nach dem Check-in Datum liegen");
        }
    }
}
