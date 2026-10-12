package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Der Hauptgast einer reservierten Buchung checkt ein. Die Rezeptionistin gibt die Zimmernummer des Hotels der
 * Buchung an, keine technische Zimmer-ID.
 */
@Builder
public record CheckeEin(@NotNull Buchung.BuchungsId buchungsId,
                        @NotNull Zimmernummer zimmernummer,
                        @NotNull Gastdaten gastdaten) implements DomainCommand {
}
