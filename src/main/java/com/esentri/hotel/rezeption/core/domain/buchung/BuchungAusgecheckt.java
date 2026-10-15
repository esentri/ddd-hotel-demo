package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

/**
 * Eine Buchung wurde ausgecheckt. Darauf hin wird das zugewiesene Zimmer geraeumt
 * (Listener: {@code ZimmerService.onBuchungAusgecheckt}).
 */
public record BuchungAusgecheckt(
    @NotNull Buchung.BuchungId buchungId,
    @NotNull Zimmer.ZimmerId zugewiesenesZimmer
) implements DomainEvent {
}
