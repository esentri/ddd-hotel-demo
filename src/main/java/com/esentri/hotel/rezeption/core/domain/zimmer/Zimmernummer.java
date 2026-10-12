package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Nummer eines Zimmers, wie sie im Hotel verwendet wird (z.B. "101", "2.14"). Nur innerhalb eines Hotels eindeutig.
 */
@Builder
public record Zimmernummer(@NotBlank @Size(max = 10) String value) implements ValueObject {
}
