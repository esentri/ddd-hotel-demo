package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LegeZimmerAn(@NotNull HotelId hotelId,
                           @NotNull Zimmernummer zimmernummer,
                           @NotNull Zimmerkategorie kategorie) implements DomainCommand {
}
