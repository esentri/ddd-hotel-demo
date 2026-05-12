package com.esentri.rezeption.domain.model.zimmer;

import com.esentri.rezeption.domain.model.Zeitraum;
import com.esentri.rezeption.domain.model.buchung.BuchungId;
import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;

public record Belegung(
    @NotNull Zeitraum zeitraum,
    @NotNull BuchungId buchungId
) implements ValueObject {
}
