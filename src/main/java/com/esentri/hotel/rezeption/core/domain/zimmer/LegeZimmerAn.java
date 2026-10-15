package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LegeZimmerAn(
    @NotNull Zimmernummer zimmernummer,
    @NotNull Zimmerkategorie zimmerkategorie,
    @NotNull Integer etage,
    @NotNull Blickrichtung blickrichtung,
    @NotNull Boolean balkon,
    @NotNull Boolean barrierefrei,
    @NotNull Bettart bettart,
    @NotNull Boolean raucherzimmer
) implements DomainCommand {
}
