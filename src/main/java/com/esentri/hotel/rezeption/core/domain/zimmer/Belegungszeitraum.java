package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.assertion.DomainAssertions;
import io.domainlifecycles.domain.types.ValueObject;
import io.domainlifecycles.validation.extend.Query;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Zeitraum, in dem ein Zimmer belegt ist: ab {@code von} bis ausschliesslich {@code bis} - wie bei einem Aufenthalt,
 * bei dem das Zimmer am Abreisetag wieder frei wird.
 */
@Builder
public record Belegungszeitraum(@NotNull LocalDate von, @NotNull LocalDate bis) implements ValueObject {

    @Override
    public void validate() {
        DomainAssertions.isTrue(von.isBefore(bis), "Eine Belegung muss vor ihrem Ende beginnen.");
    }

    @Query
    public boolean ueberschneidetSich(Belegungszeitraum anderer) {
        return von.isBefore(anderer.bis) && anderer.von.isBefore(bis);
    }

    @Query
    public boolean enthaelt(LocalDate tag) {
        return !tag.isBefore(von) && tag.isBefore(bis);
    }
}
