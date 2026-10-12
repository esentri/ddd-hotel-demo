package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Der Gast eines Zimmers checkt aus. Die Rezeptionistin nennt Hotel und Zimmernummer, keine Buchungs-ID.
 */
@Builder
public record CheckeAus(@NotNull HotelId hotelId, @NotNull Zimmernummer zimmernummer) implements DomainCommand {
}
