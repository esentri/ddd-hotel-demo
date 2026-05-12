package com.esentri.rezeption.domain.model.zimmer;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;

/**
 * Repräsentiert eine Zimmerkategorie (z.B. Einzelzimmer, Doppelzimmer, Suite).
 */
public record Zimmerkategorie(
    @NotBlank
    String name
) implements ValueObject {
}
