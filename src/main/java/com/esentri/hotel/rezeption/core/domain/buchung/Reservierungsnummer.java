package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Kennung der Reservierung im externen Reservierungssystem.
 */
@Builder
public record Reservierungsnummer(@NotBlank @Size(max = 50) String value) implements ValueObject {
}
