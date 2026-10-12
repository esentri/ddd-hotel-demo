package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record ReservierungUebernommen(@NotNull Buchung.BuchungsId buchungsId,
                                      @NotNull Reservierungsnummer reservierungsnummer) implements DomainEvent {
}
