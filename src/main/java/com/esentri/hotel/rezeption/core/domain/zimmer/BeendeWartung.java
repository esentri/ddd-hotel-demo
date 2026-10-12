package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BeendeWartung(@NotNull HotelId hotelId,
                            @NotNull Zimmernummer zimmernummer,
                            @NotNull Belegung.BelegungId belegungId) implements DomainCommand {
}
