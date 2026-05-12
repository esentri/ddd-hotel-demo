package com.esentri.rezeption.domain.model.buchung;

import io.domainlifecycles.domain.types.DomainCommand;
import jakarta.validation.constraints.NotNull;

public record CheckeGastAus(
    @NotNull BuchungId buchungId
) implements DomainCommand {
}
