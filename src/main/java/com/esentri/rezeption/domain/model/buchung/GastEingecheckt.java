package com.esentri.rezeption.domain.model.buchung;

import com.esentri.rezeption.domain.model.zimmer.ZimmerId;
import io.domainlifecycles.domain.types.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record GastEingecheckt(
    @NotNull BuchungId buchungId,
    @NotNull ZimmerId zimmerId
) implements DomainEvent {
}
