package com.esentri.rezeption.domain;

import io.domainlifecycles.domain.types.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

public record Zeitraum(LocalDate start, LocalDate ende) implements ValueObject {
    public Zeitraum {
        Objects.requireNonNull(start, "Start Datum darf nicht null sein");
        Objects.requireNonNull(ende, "Ende Datum darf nicht null sein");
        if (!ende.isAfter(start)) {
            throw new IllegalArgumentException("Ende Datum muss nach dem Start Datum liegen");
        }
    }

    /**
     * Prueft, ob sich dieser Zeitraum mit einem anderen Zeitraum ueberschneidet.
     *
     * @param zeitraum der Test-Zeitraum.
     * @return true, wenn sich die Zeitraeume ueberschneiden.
     */
    public boolean ueberlapptMit(Zeitraum zeitraum) {
        return this.start().isBefore(zeitraum.ende()) &&
                zeitraum.start().isBefore(this.ende());
    }
}
