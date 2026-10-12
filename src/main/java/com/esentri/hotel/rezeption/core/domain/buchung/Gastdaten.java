package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Die beim CheckIn geprueften, ggf. korrigierten und vervollstaendigten Daten des Hauptgasts. Name und Adresse sind
 * wegen der Meldepflicht vorgeschrieben; E-Mail und Telefonnummer sind optional.
 */
@Builder
public record Gastdaten(@NotNull Name name,
                        Email email,
                        Telefonnummer telefonnummer,
                        @NotNull Adresse adresse) implements ValueObject {
}
