package com.esentri.rezeption.domain.model.buchung;

import com.esentri.rezeption.domain.model.zimmer.ZimmerId;
import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

public record CheckeGastEin(
    @NotNull BuchungId buchungId,
    @NotNull ZimmerId zimmerId
) implements DomainCommand {
}
