package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Ein Gast ohne Reservierung checkt direkt im Hotel ein.
 */
@Builder
public record CheckeSpontanEin(@NotNull HotelId hotelId,
                               @NotNull Zimmernummer zimmernummer,
                               @NotNull Gastdaten gastdaten,
                               @NotNull Aufenthaltszeitraum aufenthaltszeitraum) implements DomainCommand {
}
