package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Anschrift eines Gastes.
 */
@Builder
public record Adresse(
    @NotBlank @Size(max = 100) String strasse,
    @NotBlank @Size(max = 10) String postleitzahl,
    @NotBlank @Size(max = 100) String ort,
    @NotBlank @Size(max = 100) String land
) implements ValueObject {
}
