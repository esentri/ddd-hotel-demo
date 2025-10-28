package com.esentri.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotEmpty;

/**
 * Die Zimmernummer, welche innerhalb eines Hotels das Zimmer eindeutig identifiziert.
 * @param wert
 */
public record ZimmerNummer(@NotEmpty String wert) implements ValueObject {
}
