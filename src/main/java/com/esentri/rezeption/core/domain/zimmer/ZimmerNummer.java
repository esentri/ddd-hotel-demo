package com.esentri.rezeption.core.domain.zimmer;

import jakarta.validation.constraints.NotEmpty;
import org.jmolecules.ddd.types.ValueObject;

/**
 * Die Zimmernummer, welche innerhalb eines Hotels das Zimmer eindeutig identifiziert.
 * @param wert
 */
public record ZimmerNummer(@NotEmpty String wert) implements ValueObject {
}
