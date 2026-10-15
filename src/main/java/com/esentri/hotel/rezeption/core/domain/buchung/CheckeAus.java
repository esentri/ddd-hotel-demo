package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Der Gast reist ab: die Buchung wird ausgecheckt, das Zimmer ist danach zu reinigen.
 */
@Builder
public record CheckeAus(@NotNull Buchung.BuchungId buchungId) implements DomainCommand {
}
